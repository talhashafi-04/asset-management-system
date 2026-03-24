# Makefile for Asset Management System (JavaFX)

# Paths
PATH_TO_FX=/opt/javafx/lib
JDBC_LIB=lib/mssql-jdbc-12.4.2.jre11.jar

# Source and output folders
SRC=src
OUT=out

# Main class
MAIN=com.assetsystem.controller.Main

# Find all java files
SOURCES=$(shell find $(SRC) -name "*.java")

.PHONY: all compile run clean fxml db-setup db-import db-seed db-start

all: compile

# Compile all Java files (Including the JDBC jar in the compilation classpath)
compile:
	@echo "Compiling Java files..."
	@mkdir -p $(OUT)
	javac --module-path $(PATH_TO_FX) --add-modules javafx.controls,javafx.fxml \
	-cp "$(JDBC_LIB)" -d $(OUT) $(SOURCES)
	@$(MAKE) fxml

# Copy FXML files to out folder (Preserving package structure)
fxml:
	@echo "Copying FXML files..."
	@find $(SRC) -name "*.fxml" | while read file; do \
		dir=$(OUT)/$$(dirname $${file#$(SRC)/}); \
		mkdir -p $$dir; \
		cp $$file $$dir; \
	done

# Run the project (Depends on compile to ensure 'out' is populated)
run: compile
	@echo "Running project..."
	java --module-path $(PATH_TO_FX) --add-modules javafx.controls,javafx.fxml \
	-cp "$(OUT):$(JDBC_LIB)" $(MAIN)


# Database management
DB_NAME=asset_db
DB_PASS=Admin@12345

# Start the SQL Server container (Fresh start)
db-setup:
	@echo "Removing old container if exists..."
	@docker rm -f $(DB_NAME) || true
	@echo "Starting SQL Server 2022..."
	docker run -e "ACCEPT_EULA=Y" -e "MSSQL_SA_PASSWORD=$(DB_PASS)" \
		-p 1433:1433 --name $(DB_NAME) \
		-d mcr.microsoft.com/mssql/server:2022-latest
	@echo "Waiting 20 seconds for boot..."
	@sleep 20
	@echo "Creating Database 'AssetSystem'..."
	docker exec -it $(DB_NAME) /opt/mssql-tools18/bin/sqlcmd \
		-S localhost -U sa -P $(DB_PASS) -C \
		-Q "CREATE DATABASE AssetSystem;"
	@echo "DB Setup Complete. Now run: make db-import"

# Import your friend's SQL file
db-import:
	@echo "Importing AS.sql..."
	docker exec -i $(DB_NAME) /opt/mssql-tools18/bin/sqlcmd \
		-S localhost -U sa -P $(DB_PASS) -C -d AssetSystem < sql/AS.sql

# Empty all tables, reload baseline + full sample data (seed_all_data.sql)
db-seed:
	@echo "Running seed_all_data.sql (truncate + reseed)..."
	docker exec -i $(DB_NAME) /opt/mssql-tools18/bin/sqlcmd \
		-S localhost -U sa -P $(DB_PASS) -C -d AssetSystem < sql/seed_all_data.sql

# Just start the container if it's stopped
db-start:
	@docker start $(DB_NAME)


# Clean the out folder
clean:
	@echo "Cleaning..."
	rm -rf $(OUT)