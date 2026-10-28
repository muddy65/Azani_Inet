# ==========================================
# Azani Billing Production WiX Installer Script
# ==========================================

$ErrorActionPreference = "Stop"
$ProjectPath = $PSScriptRoot
Set-Location $ProjectPath

Write-Host ">>> [1/6] Cleaning previous build directories..." -ForegroundColor Cyan
Remove-Item -Recurse -Force "jar_contents" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "out" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "app_input" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "build" -ErrorAction SilentlyContinue

New-Item -ItemType Directory -Path "out" | Out-Null
New-Item -ItemType Directory -Path "jar_contents" | Out-Null
New-Item -ItemType Directory -Path "app_input" | Out-Null

Write-Host ">>> [2/6] Compiling Java source files..." -ForegroundColor Cyan
$javaFiles = Get-ChildItem -Recurse -Filter "*.java" -Path "src" | Select-Object -ExpandProperty FullName
& "C:\Program Files\Java\jdk-27.0.1.0.3\bin\javac.exe" -d "out" -cp "lib/*" $javaFiles

Write-Host ">>> [3/6] Extracting dependencies (MySQL and FlatLaf) into jar_contents..." -ForegroundColor Cyan
Set-Location "jar_contents"
& "C:\Program Files\Java\jdk-27.0.1.0.3\bin\jar.exe" xf "$ProjectPath\lib\mysql-connector-j.jar"

$flatLafJar = Get-ChildItem "$ProjectPath\lib" -Filter "flatlaf-*.jar" | Select-Object -First 1
if ($flatLafJar) {
    & "C:\Program Files\Java\jdk-27.0.1.0.3\bin\jar.exe" xf $flatLafJar.FullName
} else {
    Write-Error "FlatLaf JAR not found in the lib folder!"
}
Set-Location $ProjectPath

Write-Host ">>> [4/6] Copying compiled classes and icon resources..." -ForegroundColor Cyan
Copy-Item -Recurse -Force "out\*" "jar_contents\"

if (Test-Path "src\com\azani\ui\icons") {
    New-Item -ItemType Directory -Force -Path "jar_contents\com\azani\ui\icons" | Out-Null
    Copy-Item -Recurse -Force "src\com\azani\ui\icons\*" "jar_contents\com\azani\ui\icons\"
}

Write-Host ">>> [5/6] Creating the executable Fat JAR in app_input..." -ForegroundColor Cyan
& "C:\Program Files\Java\jdk-27.0.1.0.3\bin\jar.exe" --create --file "app_input\AzaniBilling.jar" --main-class "com.azani.ui.MainDashboard" -C "jar_contents" .

Write-Host ">>> [6/6] Compiling interactive WiX installation wizard via jpackage..." -ForegroundColor Cyan
& "C:\Program Files\Java\jdk-27.0.1.0.3\bin\jpackage.exe" `
    --type exe `
    --name "AzaniBilling" `
    --app-version "1.1.0" `
    --vendor "Azani ISP" `
    --input "app_input" `
    --dest "build" `
    --main-jar "AzaniBilling.jar" `
    --win-dir-chooser `
    --win-shortcut `
    --win-menu

Write-Host "==========================================" -ForegroundColor Green
Write-Host " SUCCESS! Interactive .exe installer ready in 'build'." -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green