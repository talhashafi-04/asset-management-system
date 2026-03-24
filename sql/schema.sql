-- Reference: tables and columns expected by the Java repositories.
-- Your AssetSystem database may already define most of these; align names or change the SQL in repository classes.

-- Manufacturer(ManID, ManName)
-- Categories(CatID, CatName)
-- Models(ModelID, ModelName, ManID, CatID)
-- Allocation_Status(StatusID, StatusName)
-- AssetDetails(AssetID, SerialNo, PurchaseDate, Price, Remarks, ModelID, CurrentStatusID)

-- EmployeeRepository / AllocationRepository:
--   Employees(EmpID, FullName, Department)   -- Department VARCHAR nullable

-- UserRepository:
--   Users(UserID, Username, Password, Role)

-- AllocationRepository (optional joins):
--   Locations(LocID, LocName)
--   Asset_Allocation(
--       AllocationID, AssetID, EmpID,
--       AllocateDate, ReturnDate, Notes,
--       LocID NULL, AllocationStatusID NULL FK -> Allocation_Status
--   )

-- Example (run only if tables are missing; create core tables first):
/*
CREATE TABLE Locations (
    LocID INT IDENTITY(1,1) PRIMARY KEY,
    LocName VARCHAR(200) NOT NULL
);

CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username VARCHAR(100) NOT NULL UNIQUE,
    Password VARCHAR(200) NOT NULL,
    Role VARCHAR(50) NOT NULL
);

CREATE TABLE Employees (
    EmpID INT IDENTITY(1,1) PRIMARY KEY,
    FullName VARCHAR(200) NOT NULL,
    Department VARCHAR(200) NULL
);

CREATE TABLE Asset_Allocation (
    AllocationID INT IDENTITY(1,1) PRIMARY KEY,
    AssetID INT NOT NULL REFERENCES AssetDetails(AssetID),
    EmpID INT NOT NULL REFERENCES Employees(EmpID),
    AllocateDate DATE NOT NULL,
    ReturnDate DATE NULL,
    Notes VARCHAR(500) NULL,
    LocID INT NULL REFERENCES Locations(LocID),
    AllocationStatusID INT NULL REFERENCES Allocation_Status(StatusID)
);
*/
