# LTU Progress Bar

A custom progress bar plugin for IntelliJ IDEA and other JetBrains IDEs.

## Features

- Customizes the IDE progress bar
- [Add more features here]

## Installation

- **From JetBrains Marketplace**:
  - Open IntelliJ IDEA
  - Go to Settings/Preferences → Plugins
  - Search for "LTU Progress Bar"
  - Click Install

- **Manual Installation**:
  - Download the latest release from [GitHub Releases](https://github.com/yourusername/ltu-progress-bar/releases)
  - In IntelliJ IDEA, go to Settings/Preferences → Plugins
  - Click on the gear icon and select "Install Plugin from Disk..."
  - Select the downloaded ZIP file

## Development

### Prerequisites

- JDK 21 or higher
- IntelliJ IDEA

### Setup

1. Open the project in IntelliJ IDEA

2. Start the IDE in development mode
   ```
   ./gradlew runIde
   ```
   - IDE type is configured in `build.gradle.kts`

3. To see the changes:
    - Help -> Edit Custom Properties -> add `idea.is.internal=true`
    - Then after `gradlew runIde` task, go to: Tools -> Internal Actions -> UI -> Test Progress Indicators

### Version Management

This plugin uses semantic versioning. The version is managed in `gradle.properties` and is updated during the CI/CD process for releases.

To create a new release:

1. Create and push a tag with the version number:
   ```
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. The GitHub Actions workflow will automatically build and publish the plugin to JetBrains Marketplace.

## License

[Specify your license here]
