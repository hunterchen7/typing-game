# fresh build (no users and no scores)
./gradlew lwjgl:jar
cp ".\typing-game-fresh.db" ".\lwjgl3\build\lib\typing-game.db" # copy database to build directory
