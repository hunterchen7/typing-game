#!/bin/bash

# Delete build folder
rm -rf lwjgl3/build

# Build command
./gradlew lwjgl:jar

# Check for -f argument
# shellcheck disable=SC2199
if [[ " $@ " =~ " -f " ]]; then
    # Copy fresh database if -f is present
    cp "./typing-game-fresh.db" "./lwjgl3/build/lib/typing-game.db"
else
    # Default copy command
    cp "./typing-game.db" "./lwjgl3/build/lib"
fi
