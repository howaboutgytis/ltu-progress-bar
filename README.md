# LTU Progress Bar

For LTU devs whose hearts beat stronger.

A custom progress bar plugin for IntelliJ IDEA 2024.3

## Installation

- **From JetBrains Marketplace**:
  - In IntelliJ IDEA: Settings → Plugins
  - Search for "LTU Progress Bar"
  - Click Install

- **Manual Installation**:
  - In IntelliJ IDEA: Settings → Plugins
  - Click on the gear icon and select "Install Plugin from Disk..."
  - Select the downloaded ZIP file

## Development

### Prerequisites

- JDK 21 or higher
- IntelliJ IDEA 2024.3+

### Running

1. Start the IDE in development mode
   ```
   ./gradlew runIde
   ```
   - IDE type is configured in `build.gradle.kts`

2. One way to view the progress bar:
    - Help -> Edit Custom Properties -> add `idea.is.internal=true`
    - Then after `gradlew runIde` task, go to: Tools -> Internal Actions -> UI -> Test Progress Indicators
