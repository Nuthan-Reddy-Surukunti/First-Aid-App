# Get SHA-256 Certificate Fingerprint
# This script extracts the SHA-256 fingerprint from your Android debug keystore

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Getting SHA-256 Certificate Fingerprint" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$debugKeystore = "$env:USERPROFILE\.android\debug.keystore"

if (-not (Test-Path $debugKeystore)) {
    Write-Host "ERROR: Debug keystore not found at:" -ForegroundColor Red
    Write-Host $debugKeystore -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Please run your app at least once to generate the debug keystore." -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "✓ Found keystore at: $debugKeystore" -ForegroundColor Green
Write-Host ""

# Try to find keytool
$keytoolPaths = @(
    "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe",
    "$env:JAVA_HOME\bin\keytool.exe",
    "keytool"
)

$keytoolPath = $null
foreach ($path in $keytoolPaths) {
    try {
        if (Test-Path $path -ErrorAction SilentlyContinue) {
            $keytoolPath = $path
            break
        }
        # Try as command
        if ($path -eq "keytool") {
            $null = Get-Command keytool -ErrorAction SilentlyContinue
            if ($?) {
                $keytoolPath = "keytool"
                break
            }
        }
    } catch {
        continue
    }
}

if (-not $keytoolPath) {
    Write-Host "ERROR: Could not find keytool." -ForegroundColor Red
    Write-Host "Please make sure Android Studio is installed." -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Using keytool from: $keytoolPath" -ForegroundColor Gray
Write-Host ""
Write-Host "Extracting certificate information..." -ForegroundColor Yellow
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan

try {
    $output = & $keytoolPath -list -v -keystore $debugKeystore -alias androiddebugkey -storepass android -keypass android 2>&1

    # Display full output
    $output | ForEach-Object { Write-Host $_ }

    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""

    # Extract and highlight SHA-256
    $sha256Line = $output | Select-String "SHA256:" | Select-Object -First 1
    $sha1Line = $output | Select-String "SHA1:" | Select-Object -First 1

    if ($sha256Line) {
        Write-Host "✓ SHA-256 FINGERPRINT (Copy this for Firebase):" -ForegroundColor Green
        $fingerprint = ($sha256Line.Line -replace ".*SHA256:\s*", "").Trim()
        Write-Host $fingerprint -ForegroundColor Yellow
        Write-Host ""

        # Try to copy to clipboard
        try {
            Set-Clipboard -Value $fingerprint
            Write-Host "✓ Copied to clipboard!" -ForegroundColor Green
        } catch {
            Write-Host "⚠ Could not copy to clipboard automatically." -ForegroundColor Yellow
        }
        Write-Host ""
    }

    if ($sha1Line) {
        Write-Host "✓ SHA-1 FINGERPRINT (Optional):" -ForegroundColor Cyan
        $sha1 = ($sha1Line.Line -replace ".*SHA1:\s*", "").Trim()
        Write-Host $sha1 -ForegroundColor Gray
        Write-Host ""
    }

    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "NEXT STEPS:" -ForegroundColor Yellow
    Write-Host "1. Go to: https://console.firebase.google.com/" -ForegroundColor White
    Write-Host "2. Select your project: sample-firebase-ai-app-4a522" -ForegroundColor White
    Write-Host "3. Click gear icon > Project Settings" -ForegroundColor White
    Write-Host "4. Scroll to 'Your apps' > Click your Android app" -ForegroundColor White
    Write-Host "5. Find 'SHA certificate fingerprints'" -ForegroundColor White
    Write-Host "6. Click 'Add fingerprint' and paste the SHA-256 value above" -ForegroundColor White
    Write-Host "7. Click 'Save'" -ForegroundColor White
    Write-Host ""

} catch {
    Write-Host "ERROR: Failed to read keystore" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}

Write-Host ""
Read-Host "Press Enter to exit"

