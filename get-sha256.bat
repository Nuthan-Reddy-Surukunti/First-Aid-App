@echo off
echo ========================================
echo Getting SHA-256 Certificate Fingerprint
echo ========================================
echo.

set KEYSTORE_PATH=%USERPROFILE%\.android\debug.keystore

if not exist "%KEYSTORE_PATH%" (
    echo ERROR: Debug keystore not found at:
    echo %KEYSTORE_PATH%
    echo.
    echo Please run your app at least once to generate the debug keystore.
    echo.
    pause
    exit /b 1
)

echo Found keystore at: %KEYSTORE_PATH%
echo.
echo Extracting certificate information...
echo.

REM Try to find keytool in Android Studio
set KEYTOOL_PATH=
if exist "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" (
    set KEYTOOL_PATH=C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe
) else if exist "%JAVA_HOME%\bin\keytool.exe" (
    set KEYTOOL_PATH=%JAVA_HOME%\bin\keytool.exe
) else (
    set KEYTOOL_PATH=keytool
)

echo Using keytool: %KEYTOOL_PATH%
echo.
echo ========================================
echo.

REM Create temp file to store output
set TEMP_FILE=%TEMP%\keystore_info.txt
"%KEYTOOL_PATH%" -list -v -keystore "%KEYSTORE_PATH%" -alias androiddebugkey -storepass android -keypass android > "%TEMP_FILE%" 2>nul

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Failed to read keystore. Make sure Java/Android Studio is installed.
    echo.
    pause
    exit /b 1
)

REM Extract SHA-256 fingerprint - get everything after "SHA256: "
for /f "tokens=1,* delims=:" %%a in ('findstr /C:"SHA256:" "%TEMP_FILE%"') do (
    set SHA256_FULL=%%b
)

REM Remove leading space
set SHA256=%SHA256_FULL:~1%

REM Clean up temp file
del "%TEMP_FILE%" 2>nul

echo.
echo ========================================
echo  YOUR SHA-256 CERTIFICATE FINGERPRINT
echo ========================================
echo.
echo Copy this EXACT value and paste it into Firebase:
echo.
echo %SHA256%
echo.
echo ========================================
echo.
echo IMPORTANT: Copy ONLY the fingerprint above ^(the line with colons^)
echo DO NOT include "SHA256:" or any other text
echo.
echo Then add it to Firebase Console:
echo 1. Go to: https://console.firebase.google.com/
echo 2. Select your project: sample-firebase-ai-app-4a522
echo 3. Project Settings ^> Your Android App
echo 4. Click "Add fingerprint" and paste the SHA-256 value
echo.
pause

