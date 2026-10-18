param(
    [string]$TomcatHome = $env:CATALINA_HOME
)

if ([string]::IsNullOrWhiteSpace($TomcatHome)) {
    throw "Informe o caminho do Tomcat com -TomcatHome."
}

$environmentFile = Join-Path $PSScriptRoot ".env"
$catalinaScript = Join-Path $TomcatHome "bin\catalina.bat"

if (-not (Test-Path -LiteralPath $environmentFile)) {
    throw "Arquivo .env não encontrado na raiz do projeto."
}

if (-not (Test-Path -LiteralPath $catalinaScript)) {
    throw "catalina.bat não encontrado no caminho informado."
}

$javaHome = $env:JAVA_HOME
if ([string]::IsNullOrWhiteSpace($javaHome) -or
        -not (Test-Path -LiteralPath (Join-Path $javaHome "bin\java.exe"))) {
    $javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($null -eq $javaCommand) {
        throw "JAVA_HOME não está definido e java.exe não foi encontrado no PATH."
    }
    $javaSettings = & $javaCommand.Source -XshowSettings:properties -version 2>&1
    $javaHomeLine = $javaSettings |
        Select-String -Pattern '^\s*java\.home\s*=' |
        Select-Object -First 1
    if ($null -ne $javaHomeLine) {
        $javaHome = ($javaHomeLine.Line -replace '^\s*java\.home\s*=\s*', '').Trim()
    }
}

if (-not (Test-Path -LiteralPath (Join-Path $javaHome "bin\java.exe"))) {
    throw "JAVA_HOME não aponta para uma instalação válida do Java."
}

$databaseLine = Get-Content -LiteralPath $environmentFile |
    Where-Object { $_ -match '^\s*DATABASE_URL\s*=' } |
    Select-Object -Last 1

if ([string]::IsNullOrWhiteSpace($databaseLine)) {
    throw "DATABASE_URL não foi encontrada no .env."
}

$databaseUrl = ($databaseLine -replace '^\s*DATABASE_URL\s*=\s*', '').Trim()
if ($databaseUrl.Length -ge 2 -and
        (($databaseUrl.StartsWith('"') -and $databaseUrl.EndsWith('"')) -or
         ($databaseUrl.StartsWith("'") -and $databaseUrl.EndsWith("'")))) {
    $databaseUrl = $databaseUrl.Substring(1, $databaseUrl.Length - 2)
}

if ([string]::IsNullOrWhiteSpace($databaseUrl)) {
    throw "DATABASE_URL está vazia no .env."
}

$previousDatabaseUrl = $env:DATABASE_URL
$previousEnvFile = $env:APP_ENV_FILE
$previousJavaHome = $env:JAVA_HOME
$previousCatalinaHome = $env:CATALINA_HOME
$previousCatalinaBase = $env:CATALINA_BASE
$projectDirectory = Get-Location

try {
    $env:DATABASE_URL = $databaseUrl
    $env:APP_ENV_FILE = $environmentFile
    $env:JAVA_HOME = $javaHome
    $env:CATALINA_HOME = $TomcatHome
    $env:CATALINA_BASE = $TomcatHome
    Set-Location -LiteralPath $PSScriptRoot
    & $catalinaScript run
} finally {
    if ($null -eq $previousDatabaseUrl) {
        Remove-Item Env:DATABASE_URL -ErrorAction SilentlyContinue
    } else {
        $env:DATABASE_URL = $previousDatabaseUrl
    }
    if ($null -eq $previousEnvFile) {
        Remove-Item Env:APP_ENV_FILE -ErrorAction SilentlyContinue
    } else {
        $env:APP_ENV_FILE = $previousEnvFile
    }
    if ($null -eq $previousJavaHome) {
        Remove-Item Env:JAVA_HOME -ErrorAction SilentlyContinue
    } else {
        $env:JAVA_HOME = $previousJavaHome
    }
    if ($null -eq $previousCatalinaHome) {
        Remove-Item Env:CATALINA_HOME -ErrorAction SilentlyContinue
    } else {
        $env:CATALINA_HOME = $previousCatalinaHome
    }
    if ($null -eq $previousCatalinaBase) {
        Remove-Item Env:CATALINA_BASE -ErrorAction SilentlyContinue
    } else {
        $env:CATALINA_BASE = $previousCatalinaBase
    }
    Set-Location -LiteralPath $projectDirectory
}