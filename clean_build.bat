@echo off
echo Cleaning and rebuilding FirstAid App...
cd /d "C:\Users\Nuthan Reddy\FirstAidApp"

echo Step 1: Cleaning project...
call gradlew clean

echo Step 2: Rebuilding project...
call gradlew build

echo Build complete!
pause
