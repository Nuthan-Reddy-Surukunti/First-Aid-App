@echo off
echo Cleaning build artifacts for JDK 17 compatibility...

REM Clean Gradle caches
echo Cleaning Gradle caches...
gradlew clean
gradlew --stop

REM Clean local build directories
echo Cleaning local build directories...
rmdir /s /q build 2>nul
rmdir /s /q app\build 2>nul
rmdir /s /q .gradle 2>nul

REM Clean specific transform caches that cause JDK issues
echo Cleaning transform caches...
rmdir /s /q "%USERPROFILE%\.gradle\caches\transforms-3" 2>nul
rmdir /s /q "%USERPROFILE%\.gradle\caches\8.13\transforms" 2>nul

echo Clean complete! Now building with JDK 17 configuration...
gradlew assembleDebug

echo Build complete!
pause
