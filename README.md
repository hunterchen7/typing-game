# typing-game-libgdx

This is a typing game built with [LibGDX](https://libgdx.com/), generated with [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff) using [OpenJDK 21](https://www.oracle.com/ca-en/java/technologies/downloads/#java21).

![demo](https://github.com/hunterchen7/hunterchen7/assets/34012681/93987f2d-32e5-4712-b144-23ce174d9fe4)

Users can play 30 levels of varying difficulty containing thousands of unique words, while competing for a spot on the leaderboard.

User, level and score data are all stored in a SQLite database.

Demo video: https://www.youtube.com/watch?v=1PN8l_tNcNQ

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Development setup guide
1. Install [JDK 21](https://www.oracle.com/ca-en/java/technologies/downloads/#java21)
2. Clone repository
3. Open project in IntelliJ
4. Run/Debug `Lwjgl3Launcher.java`
5. Alternatively configure as an application using OpenJDK 21, classpath = `group9.lwjgl3.main` and main method = `com.cs2212group9.typinggame.lwjgl3.Lwjgl3Launcher`
- Most pages are configured with hot-reload: use `Ctrl + F9` to recompile and `Ctrl + Shift + F5` to reload the page
- To modify level generation, Python is required

## Build guide
- Run `./build.sh` to build the project with pre-existing user data
- Run `./build.sh -f` to build the project with a fresh database
- Alternatively, use `./build.bat` if bash not available
- Both will build to `lwjgl3/build/libs/`, run the game with `typing-game-libgdx-x.x.x.jar`
- using `./gradlew lwjgl3:jar` will build the project, but will not copy the database, so it will not work
- Python is NOT required to build the project, but is used for level generation, [Python 3.11](https://www.python.org/downloads/release/python-3112/) was used.

## Testing
- Run tests by configuring `JUnit` in IntelliJ to run all in package `com.cs2212group9.typinggame`

## Instructor Mode
- To log in as an instructor/admin, use the username `admin`. The password is the same as the username.
- As an instructor, you see the same scoreboard as everyone else, but you have an additional search function.
- It allows you to search for users by username, and view their scores and number of attempts on each level.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3.

## Gradle

This project uses [Gradle](http://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/lib`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

## Credits
- Libraries used:
  - [libGDX](https://libgdx.com/)
  - [sqlite-jdbc](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc)
  - [junit-jupiter-api](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api)
  - [slf4j-api](https://mvnrepository.com/artifact/org.slf4j/slf4j-api)
  - [slf4j-simple](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple)
  - [bouncycastle](https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on)
- Images:
  - level and score screen background [freepik](https://www.freepik.com/free-photo/aerial-shot-beautiful-landscape-covered-with-snow-early-morning_11061964.htm#fromView=search&page=1&position=2&uuid=60e3e85d-534e-4371-a570-af27897804c6)
  - login screen [freepik](https://www.freepik.com/free-ai-image/view-planet-earth-sunrise-from-space_43168360.htm#fromView=search&page=1&position=41&uuid=0bb51760-41eb-435b-a5ab-e5b905d36678)
  - game screen [freepik](https://www.freepik.com/free-ai-image/exploration-majestic-galaxy-through-space-shuttle-technology-generated-by-ai_47596883.htm#fromView=search&page=3&position=23&uuid=64b6abd9-398f-)
  - explosion(s) by [Sinestesia](https://opengameart.org/content/2d-explosion-animations-frame-by-frame)
  - asteroids by [Wenrexa](https://opengameart.org/content/asteroids-pack-n01)
  - app icon from [opengameart](https://opengameart.org/content/dino-spaceship-flying-character)
- Audio:
  - sound effects (e.g. explosion sounds) from [Kenney](https://kenney.nl/)
  - main menu music by [lasercheese](https://opengameart.org/content/space-orchestral)
  - game music 1: mammoth by [congusbongus](https://opengameart.org/content/mammoth)
  - game music 2: action synth track by [PetterTheSturgeon](https://opengameart.org/content/action-synth-track)
  - game music 3: deus ex tempus by [Trevor Lentz](https://opengameart.org/content/deus-ex-tempus)
  - game music 4: last knight of the cyberdeath [PetterTheSturgeon](https://opengameart.org/content/lastknightofthecyberdeath)
  - game music 5: magic space by [CodeManu](https://opengameart.org/content/magic-space)
  - level and score screen music: space echo by [Centurion_of_war](https://opengameart.org/content/space-echo)
- Misc:
  - libgdx-multiplayer-authentication-flow used with permission from [szsascha](https://github.com/szsascha/libgdx-multiplayer-authentication-flow/issues/1)
  - explosion animation based off of [libgdx-2d-tutorial](https://github.com/hollowbit/libgdx-2d-tutorial)
  - setup generated using [gdx-liftoff](https://github.com/libgdx/gdx-liftoff)
  - English word list from [google-10000-english](https://github.com/first20hours/google-10000-english)
  - Some additional words generated by ChatGPT, see the Python file for more details
  - LibGDX skins from [czyzby](https://github.com/czyzby/gdx-skins)
