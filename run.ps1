$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
# Opcional: configuración privada de esta máquina; nunca se versiona.
$localSecrets = Join-Path $PSScriptRoot '.local/secrets.json'
if (Test-Path -LiteralPath $localSecrets) {
    $settings = Get-Content -LiteralPath $localSecrets -Raw | ConvertFrom-Json
    if (-not $env:DB_PASSWORD) { $env:DB_PASSWORD = $settings.DB_PASSWORD }
    if (-not $env:JWT_SECRET) { $env:JWT_SECRET = $settings.JWT_SECRET }
}
if (-not $env:DB_PASSWORD -or -not $env:JWT_SECRET) {
    throw 'Configure DB_PASSWORD y JWT_SECRET en el entorno antes de iniciar. Consulte README.md.'
}
if (-not $env:JAVA_HOME) {
    $jdk = Get-ChildItem -LiteralPath (Join-Path $env:USERPROFILE '.jdks') -Directory -ErrorAction SilentlyContinue |
        Where-Object Name -Match '21\.' | Select-Object -First 1
    if ($jdk) { $env:JAVA_HOME = $jdk.FullName }
}
& "$PSScriptRoot/mvnw.cmd" spring-boot:run
exit $LASTEXITCODE
