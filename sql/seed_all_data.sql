/*
  1) Empties all application tables (FK-safe order).
  2) Resets identity seeds.
  3) Loads baseline reference rows (same intent as sql/AS.sql).
  4) Loads supplemental users, employees, models, assets, allocations.

  Run against an existing AssetSystem database (schema from AS.sql).
*/

USE AssetSystem;
GO

SET NOCOUNT ON;

/* ========== EMPTY ALL TABLES ========== */
DELETE FROM dbo.Asset_Allocation;
DELETE FROM dbo.AssetDetails;
DELETE FROM dbo.Employees;
DELETE FROM dbo.Models;
DELETE FROM dbo.Users;
DELETE FROM dbo.Categories;
DELETE FROM dbo.Manufacturer;
DELETE FROM dbo.Departments;
DELETE FROM dbo.Locations;
DELETE FROM dbo.Allocation_Status;
GO

DBCC CHECKIDENT ('dbo.Asset_Allocation', RESEED, 0);
DBCC CHECKIDENT ('dbo.AssetDetails', RESEED, 0);
DBCC CHECKIDENT ('dbo.Employees', RESEED, 0);
DBCC CHECKIDENT ('dbo.Models', RESEED, 0);
DBCC CHECKIDENT ('dbo.Users', RESEED, 0);
DBCC CHECKIDENT ('dbo.Categories', RESEED, 0);
DBCC CHECKIDENT ('dbo.Manufacturer', RESEED, 0);
DBCC CHECKIDENT ('dbo.Departments', RESEED, 0);
DBCC CHECKIDENT ('dbo.Locations', RESEED, 0);
DBCC CHECKIDENT ('dbo.Allocation_Status', RESEED, 0);
GO

/* ========== BASELINE (aligned with AS.sql) ========== */
SET IDENTITY_INSERT [dbo].[Allocation_Status] ON;
INSERT [dbo].[Allocation_Status] ([StatusID], [StatusName]) VALUES (1, N'In Store');
INSERT [dbo].[Allocation_Status] ([StatusID], [StatusName]) VALUES (2, N'Allocated');
INSERT [dbo].[Allocation_Status] ([StatusID], [StatusName]) VALUES (3, N'Repair');
INSERT [dbo].[Allocation_Status] ([StatusID], [StatusName]) VALUES (4, N'Scrapped');
SET IDENTITY_INSERT [dbo].[Allocation_Status] OFF;

SET IDENTITY_INSERT [dbo].[Categories] ON;
INSERT [dbo].[Categories] ([CatID], [CatName]) VALUES (1, N'Laptop');
INSERT [dbo].[Categories] ([CatID], [CatName]) VALUES (2, N'Monitor');
INSERT [dbo].[Categories] ([CatID], [CatName]) VALUES (3, N'Keyboard');
INSERT [dbo].[Categories] ([CatID], [CatName]) VALUES (4, N'Mouse');
INSERT [dbo].[Categories] ([CatID], [CatName]) VALUES (5, N'Laptop');
SET IDENTITY_INSERT [dbo].[Categories] OFF;

SET IDENTITY_INSERT [dbo].[Departments] ON;
INSERT [dbo].[Departments] ([DeptID], [DeptName]) VALUES (1, N'Software');
INSERT [dbo].[Departments] ([DeptID], [DeptName]) VALUES (2, N'HR');
INSERT [dbo].[Departments] ([DeptID], [DeptName]) VALUES (3, N'Finance');
SET IDENTITY_INSERT [dbo].[Departments] OFF;

SET IDENTITY_INSERT [dbo].[Locations] ON;
INSERT [dbo].[Locations] ([LocID], [LocName]) VALUES (1, N'IT Store');
INSERT [dbo].[Locations] ([LocID], [LocName]) VALUES (2, N'Floor 1');
INSERT [dbo].[Locations] ([LocID], [LocName]) VALUES (3, N'Floor 2');
SET IDENTITY_INSERT [dbo].[Locations] OFF;

SET IDENTITY_INSERT [dbo].[Manufacturer] ON;
INSERT [dbo].[Manufacturer] ([ManID], [ManName]) VALUES (1, N'Dell');
INSERT [dbo].[Manufacturer] ([ManID], [ManName]) VALUES (2, N'HP');
INSERT [dbo].[Manufacturer] ([ManID], [ManName]) VALUES (3, N'Apple');
INSERT [dbo].[Manufacturer] ([ManID], [ManName]) VALUES (4, N'Logitech');
INSERT [dbo].[Manufacturer] ([ManID], [ManName]) VALUES (5, N'Dell');
SET IDENTITY_INSERT [dbo].[Manufacturer] OFF;

SET IDENTITY_INSERT [dbo].[Models] ON;
INSERT [dbo].[Models] ([ModelID], [ModelName], [CatID], [ManID]) VALUES (1, N'Latitude 5420', 1, 1);
SET IDENTITY_INSERT [dbo].[Models] OFF;
GO

/* ========== SUPPLEMENTAL SEED ========== */
SET NOCOUNT ON;

INSERT INTO dbo.Users (Username, Password, Role) VALUES (N'admin', N'admin123', N'Admin');
INSERT INTO dbo.Users (Username, Password, Role) VALUES (N'alice', N'pass123', N'Staff');
INSERT INTO dbo.Users (Username, Password, Role) VALUES (N'bob', N'pass123', N'Staff');
INSERT INTO dbo.Users (Username, Password, Role) VALUES (N'carol', N'pass123', N'Staff');

INSERT INTO dbo.Categories (CatName) VALUES (N'Docking Station');
INSERT INTO dbo.Manufacturer (ManName) VALUES (N'Lenovo');

DECLARE @CatDock INT = (SELECT CatID FROM dbo.Categories WHERE CatName = N'Docking Station');
DECLARE @ManLenovo INT = (SELECT ManID FROM dbo.Manufacturer WHERE ManName = N'Lenovo');
DECLARE @CatLaptop INT = (SELECT TOP 1 CatID FROM dbo.Categories WHERE CatName = N'Laptop' ORDER BY CatID);
DECLARE @ManDell INT = (SELECT TOP 1 ManID FROM dbo.Manufacturer WHERE ManName = N'Dell' ORDER BY ManID);
DECLARE @ManHP INT = (SELECT TOP 1 ManID FROM dbo.Manufacturer WHERE ManName = N'HP' ORDER BY ManID);

INSERT INTO dbo.Models (ModelName, CatID, ManID) VALUES (N'ProBook 450 G9', @CatLaptop, @ManHP);
INSERT INTO dbo.Models (ModelName, CatID, ManID) VALUES (N'ThinkPad X1', @CatLaptop, @ManLenovo);
INSERT INTO dbo.Models (ModelName, CatID, ManID) VALUES (N'WD19 Dock', @CatDock, @ManDell);

DECLARE @ModelProBook INT = (SELECT ModelID FROM dbo.Models WHERE ModelName = N'ProBook 450 G9');
DECLARE @ModelThinkPad INT = (SELECT ModelID FROM dbo.Models WHERE ModelName = N'ThinkPad X1');
DECLARE @ModelDock INT = (SELECT ModelID FROM dbo.Models WHERE ModelName = N'WD19 Dock');
DECLARE @ModelLatitude INT = (SELECT ModelID FROM dbo.Models WHERE ModelName = N'Latitude 5420');

INSERT INTO dbo.Employees (EmpName, DeptID) VALUES (N'Alice Smith', 1);
INSERT INTO dbo.Employees (EmpName, DeptID) VALUES (N'Bob Jones', 2);
INSERT INTO dbo.Employees (EmpName, DeptID) VALUES (N'Carol White', 3);
INSERT INTO dbo.Employees (EmpName, DeptID) VALUES (N'David Chen', 1);

DECLARE @EmpAlice INT = (SELECT EmpID FROM dbo.Employees WHERE EmpName = N'Alice Smith');
DECLARE @EmpBob INT = (SELECT EmpID FROM dbo.Employees WHERE EmpName = N'Bob Jones');

DECLARE @StatusInStore INT = (SELECT StatusID FROM dbo.Allocation_Status WHERE StatusName = N'In Store');
DECLARE @StatusAllocated INT = (SELECT StatusID FROM dbo.Allocation_Status WHERE StatusName = N'Allocated');
DECLARE @StatusRepair INT = (SELECT StatusID FROM dbo.Allocation_Status WHERE StatusName = N'Repair');

INSERT INTO dbo.AssetDetails (ModelID, SerialNo, PurchaseDate, Price, CurrentStatusID, Remarks)
VALUES (@ModelProBook, N'SN-HP-10001', CAST(N'2024-06-01' AS DATE), CAST(899.00 AS DECIMAL(18,2)), @StatusInStore, N'Seed: in store');

INSERT INTO dbo.AssetDetails (ModelID, SerialNo, PurchaseDate, Price, CurrentStatusID, Remarks)
VALUES (@ModelThinkPad, N'SN-LEN-20002', CAST(N'2024-07-10' AS DATE), CAST(1450.00 AS DECIMAL(18,2)), @StatusInStore, N'Seed: in store');

INSERT INTO dbo.AssetDetails (ModelID, SerialNo, PurchaseDate, Price, CurrentStatusID, Remarks)
VALUES (@ModelLatitude, N'SN-ALLOC-30001', CAST(N'2024-03-01' AS DATE), CAST(1200.50 AS DECIMAL(18,2)), @StatusAllocated, N'Seed: allocated');

INSERT INTO dbo.AssetDetails (ModelID, SerialNo, PurchaseDate, Price, CurrentStatusID, Remarks)
VALUES (@ModelProBook, N'SN-REPAIR-40001', CAST(N'2023-11-20' AS DATE), CAST(899.00 AS DECIMAL(18,2)), @StatusRepair, N'Seed: in repair');

INSERT INTO dbo.AssetDetails (ModelID, SerialNo, PurchaseDate, Price, CurrentStatusID, Remarks)
VALUES (@ModelDock, N'SN-DOCK-50001', CAST(N'2024-02-15' AS DATE), CAST(199.99 AS DECIMAL(18,2)), @StatusInStore, N'Seed: dock in store');

DECLARE @AssetAlloc INT = (SELECT AssetID FROM dbo.AssetDetails WHERE SerialNo = N'SN-ALLOC-30001');
DECLARE @AssetRepair INT = (SELECT AssetID FROM dbo.AssetDetails WHERE SerialNo = N'SN-REPAIR-40001');

INSERT INTO dbo.Asset_Allocation (AssetID, EmpID, StatusID, AllocationDate, ReturnDate, Notes, LocID, DeptID)
VALUES (@AssetAlloc, @EmpAlice, @StatusAllocated, CAST(N'2024-09-01' AS DATETIME), NULL, N'Seed allocation to Alice', 2, 1);

INSERT INTO dbo.Asset_Allocation (AssetID, EmpID, StatusID, AllocationDate, ReturnDate, Notes, LocID, DeptID)
VALUES (@AssetRepair, @EmpBob, @StatusAllocated, CAST(N'2024-01-10' AS DATETIME), CAST(N'2024-08-01' AS DATETIME), N'Returned - seed history', 1, 2);

PRINT N'seed_all_data.sql completed (empty + baseline + supplemental).';
GO
