@echo off
REM Build script that uses the same environment as Android Studio
REM This ensures terminal builds match Android Studio builds

setlocal enabledelayedexpansion

REM Try multiple common Android Studio installation paths
set "PATHS_TO_TRY[0]=C:\Program Files\Android\Android Studio\jbr"
set "PATHS_TO_TRY[1]=C:\Program Files (x86)\Android\Android Studio\jbr"
set "PATHS_TO_TRY[2]=C:\Program Files\Android\Android Studio Preview\jbr"

set "JBR_FOUND="

for /L %%i in (0,1,2) do (
    if not defined JBR_FOUND (
        if exist "!PATHS_TO_TRY[%%i]!" (
            set "JBR_FOUND=!PATHS_TO_TRY[%%i]!"
        )
    )
)

if defined JBR_FOUND (
    echo [INFO] Found Android Studio JBR at: %JBR_FOUND%
    set "JAVA_HOME=%JBR_FOUND%"
    set "PATH=%JBR_FOUND%\bin;%PATH%"
    echo [INFO] Using Java: %JBR_FOUND%\bin\java.exe
    %JBR_FOUND%\bin\java.exe -version
    echo.
    echo [INFO] Building with same JDK as Android Studio...
    echo.
    call gradlew.bat %*
) else (
    echo [ERROR] Android Studio JBR not found in any standard location
    echo [ERROR] Tried:
    echo   - C:\Program Files\Android\Android Studio\jbr
    echo   - C:\Program Files (x86)\Android\Android Studio\jbr
    echo   - C:\Program Files\Android\Android Studio Preview\jbr
    echo.
    echo [SOLUTION] Please ensure Android Studio is installed, or:
    echo   1. Install Java 17 JDK from: https://www.oracle.com/java/technologies/downloads/
    echo   2. Add Java to your system PATH
    echo.
    pause
    exit /b 1
)

