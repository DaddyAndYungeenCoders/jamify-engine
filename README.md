# jamify-engine
The jamify main backend.
## Qualité du Code

### Couverture des Tests
![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

Pour plus de détails, consultez notre [rapport de couverture détaillé](.github/test-coverage/coverage.md).
## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Introduction

Jamify is a music streaming application that allows users to listen to their favorite songs, create playlists, and discover new music. The jamify-engine is the main backend service that handles music playback, search, and recommendation features.

## Features

- Discover Jam
- Participate to events
- Create and share playlists

## Requirements

- Java 21
- Maven 3.6 or higher
- PostgreSQL

## Installation

1. Clone the repository:
    ```sh
    git clone https://https://github.com/DaddyAndYungeenCoders/jamify-engine
    cd jamify-engine
    ```

2. Build the project:
    ```sh
    mvn clean install
    ```

3. Set up the PostgreSQL database using Docker:
    ```sh
    docker compose -f docker/compose.yml up -d
    ```

## Configuration

1. Ensure the following environment variables are set in your Docker or system environment:
    ```sh
    export POSTGRES_USER=your_postgres_user
    export POSTGRES_PASSWORD=your_postgres_password
    export POSTGRES_DB=your_postgres_db
    export POSTGRES_HOST=your_postgres_host eg. localhost
    export POSTGRES_PORT=your_postgres_port eg. 5432
    export JAMIFY_UAA_BASE_URL=http://localhost:8081
    export JAMIFY_ENGINE_API_KEY=your_jamify_engine_api_key
    ```

## Usage

1. Run the application with the `dev` profile in local development:
    ```sh
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    ```

2. Access the application at `http://localhost:8082`.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.
