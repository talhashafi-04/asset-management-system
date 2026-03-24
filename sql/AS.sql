USE [master]
GO
/****** Object:  Database [AssetSystem]    Script Date: 24-Mar-26 10:05:47 PM ******/
CREATE DATABASE [AssetSystem]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'AssetSystem', FILENAME = N'D:\SQL2017\MSSQL14.SQLEXPRESS\MSSQL\DATA\AssetSystem.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'AssetSystem_log', FILENAME = N'D:\SQL2017\MSSQL14.SQLEXPRESS\MSSQL\DATA\AssetSystem_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
GO
ALTER DATABASE [AssetSystem] SET COMPATIBILITY_LEVEL = 140
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [AssetSystem].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [AssetSystem] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [AssetSystem] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [AssetSystem] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [AssetSystem] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [AssetSystem] SET ARITHABORT OFF 
GO
ALTER DATABASE [AssetSystem] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [AssetSystem] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [AssetSystem] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [AssetSystem] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [AssetSystem] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [AssetSystem] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [AssetSystem] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [AssetSystem] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [AssetSystem] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [AssetSystem] SET  ENABLE_BROKER 
GO
ALTER DATABASE [AssetSystem] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [AssetSystem] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [AssetSystem] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [AssetSystem] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [AssetSystem] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [AssetSystem] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [AssetSystem] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [AssetSystem] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [AssetSystem] SET  MULTI_USER 
GO
ALTER DATABASE [AssetSystem] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [AssetSystem] SET DB_CHAINING OFF 
GO
ALTER DATABASE [AssetSystem] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [AssetSystem] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [AssetSystem] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [AssetSystem] SET QUERY_STORE = OFF
GO
USE [AssetSystem]
GO
/****** Object:  Table [dbo].[Allocation_Status]    Script Date: 24-Mar-26 10:05:47 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Allocation_Status](
	[StatusID] [int] IDENTITY(1,1) NOT NULL,
	[StatusName] [varchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[StatusID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Asset_Allocation]    Script Date: 24-Mar-26 10:05:47 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Asset_Allocation](
	[AllocID] [int] IDENTITY(1,1) NOT NULL,
	[AssetID] [int] NULL,
	[EmpID] [int] NULL,
	[StatusID] [int] NULL,
	[AllocationDate] [datetime] NULL,
	[ReturnDate] [datetime] NULL,
	[Notes] [nvarchar](max) NULL,
	[LocID] [int] NULL,
	[DeptID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[AllocID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[AssetDetails]    Script Date: 24-Mar-26 10:05:47 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[AssetDetails](
	[AssetID] [int] IDENTITY(1,1) NOT NULL,
	[ModelID] [int] NULL,
	[SerialNo] [varchar](100) NOT NULL,
	[PurchaseDate] [date] NULL,
	[Price] [decimal](18, 2) NULL,
	[CurrentStatusID] [int] NULL,
	[Remarks] [nvarchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[AssetID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Categories]    Script Date: 24-Mar-26 10:05:47 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Categories](
	[CatID] [int] IDENTITY(1,1) NOT NULL,
	[CatName] [varchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[CatID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Departments]    Script Date: 24-Mar-26 10:05:47 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Departments](
	[DeptID] [int] IDENTITY(1,1) NOT NULL,
	[DeptName] [varchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[DeptID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Employees]    Script Date: 24-Mar-26 10:05:47 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Employees](
	[EmpID] [int] IDENTITY(1,1) NOT NULL,
	[EmpName] [varchar](100) NOT NULL,
	[DeptID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[EmpID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Locations]    Script Date: 24-Mar-26 10:05:47 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Locations](
	[LocID] [int] IDENTITY(1,1) NOT NULL,
	[LocName] [varchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[LocID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Manufacturer]    Script Date: 24-Mar-26 10:05:47 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Manufacturer](
	[ManID] [int] IDENTITY(1,1) NOT NULL,
	[ManName] [varchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ManID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Models]    Script Date: 24-Mar-26 10:05:47 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Models](
	[ModelID] [int] IDENTITY(1,1) NOT NULL,
	[ModelName] [varchar](100) NOT NULL,
	[CatID] [int] NULL,
	[ManID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[ModelID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 24-Mar-26 10:05:47 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[UserID] [int] IDENTITY(1,1) NOT NULL,
	[Username] [varchar](50) NOT NULL,
	[Password] [varchar](255) NOT NULL,
	[Role] [varchar](20) NULL,
PRIMARY KEY CLUSTERED 
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[Allocation_Status] ON 

INSERT [dbo].[Allocation_Status] ([StatusID], [StatusName]) VALUES (1, N'In Store')
INSERT [dbo].[Allocation_Status] ([StatusID], [StatusName]) VALUES (2, N'Allocated')
INSERT [dbo].[Allocation_Status] ([StatusID], [StatusName]) VALUES (3, N'Repair')
INSERT [dbo].[Allocation_Status] ([StatusID], [StatusName]) VALUES (4, N'Scrapped')
SET IDENTITY_INSERT [dbo].[Allocation_Status] OFF
GO
SET IDENTITY_INSERT [dbo].[AssetDetails] ON 

INSERT [dbo].[AssetDetails] ([AssetID], [ModelID], [SerialNo], [PurchaseDate], [Price], [CurrentStatusID], [Remarks]) VALUES (2, 1, N'SN-DELL-9900', CAST(N'2024-01-15' AS Date), CAST(1200.50 AS Decimal(18, 2)), 1, N'Initial test asset')
SET IDENTITY_INSERT [dbo].[AssetDetails] OFF
GO
SET IDENTITY_INSERT [dbo].[Categories] ON 

INSERT [dbo].[Categories] ([CatID], [CatName]) VALUES (1, N'Laptop')
INSERT [dbo].[Categories] ([CatID], [CatName]) VALUES (2, N'Monitor')
INSERT [dbo].[Categories] ([CatID], [CatName]) VALUES (3, N'Keyboard')
INSERT [dbo].[Categories] ([CatID], [CatName]) VALUES (4, N'Mouse')
INSERT [dbo].[Categories] ([CatID], [CatName]) VALUES (5, N'Laptop')
SET IDENTITY_INSERT [dbo].[Categories] OFF
GO
SET IDENTITY_INSERT [dbo].[Departments] ON 

INSERT [dbo].[Departments] ([DeptID], [DeptName]) VALUES (1, N'Software')
INSERT [dbo].[Departments] ([DeptID], [DeptName]) VALUES (2, N'HR')
INSERT [dbo].[Departments] ([DeptID], [DeptName]) VALUES (3, N'Finance')
SET IDENTITY_INSERT [dbo].[Departments] OFF
GO
SET IDENTITY_INSERT [dbo].[Locations] ON 

INSERT [dbo].[Locations] ([LocID], [LocName]) VALUES (1, N'IT Store')
INSERT [dbo].[Locations] ([LocID], [LocName]) VALUES (2, N'Floor 1')
INSERT [dbo].[Locations] ([LocID], [LocName]) VALUES (3, N'Floor 2')
SET IDENTITY_INSERT [dbo].[Locations] OFF
GO
SET IDENTITY_INSERT [dbo].[Manufacturer] ON 

INSERT [dbo].[Manufacturer] ([ManID], [ManName]) VALUES (1, N'Dell')
INSERT [dbo].[Manufacturer] ([ManID], [ManName]) VALUES (2, N'HP')
INSERT [dbo].[Manufacturer] ([ManID], [ManName]) VALUES (3, N'Apple')
INSERT [dbo].[Manufacturer] ([ManID], [ManName]) VALUES (4, N'Logitech')
INSERT [dbo].[Manufacturer] ([ManID], [ManName]) VALUES (5, N'Dell')
SET IDENTITY_INSERT [dbo].[Manufacturer] OFF
GO
SET IDENTITY_INSERT [dbo].[Models] ON 

INSERT [dbo].[Models] ([ModelID], [ModelName], [CatID], [ManID]) VALUES (1, N'Latitude 5420', 1, 1)
SET IDENTITY_INSERT [dbo].[Models] OFF
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__AssetDet__5E5A535EE19A87DE]    Script Date: 24-Mar-26 10:05:47 PM ******/
ALTER TABLE [dbo].[AssetDetails] ADD UNIQUE NONCLUSTERED 
(
	[SerialNo] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__Users__536C85E48D667BA5]    Script Date: 24-Mar-26 10:05:47 PM ******/
ALTER TABLE [dbo].[Users] ADD UNIQUE NONCLUSTERED 
(
	[Username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Asset_Allocation] ADD  DEFAULT (getdate()) FOR [AllocationDate]
GO
ALTER TABLE [dbo].[Asset_Allocation]  WITH CHECK ADD FOREIGN KEY([AssetID])
REFERENCES [dbo].[AssetDetails] ([AssetID])
GO
ALTER TABLE [dbo].[Asset_Allocation]  WITH CHECK ADD FOREIGN KEY([EmpID])
REFERENCES [dbo].[Employees] ([EmpID])
GO
ALTER TABLE [dbo].[Asset_Allocation]  WITH CHECK ADD FOREIGN KEY([StatusID])
REFERENCES [dbo].[Allocation_Status] ([StatusID])
GO
ALTER TABLE [dbo].[Asset_Allocation]  WITH CHECK ADD  CONSTRAINT [FK_Allocation_Department] FOREIGN KEY([DeptID])
REFERENCES [dbo].[Departments] ([DeptID])
GO
ALTER TABLE [dbo].[Asset_Allocation] CHECK CONSTRAINT [FK_Allocation_Department]
GO
ALTER TABLE [dbo].[Asset_Allocation]  WITH CHECK ADD  CONSTRAINT [FK_Allocation_Location] FOREIGN KEY([LocID])
REFERENCES [dbo].[Locations] ([LocID])
GO
ALTER TABLE [dbo].[Asset_Allocation] CHECK CONSTRAINT [FK_Allocation_Location]
GO
ALTER TABLE [dbo].[AssetDetails]  WITH CHECK ADD FOREIGN KEY([CurrentStatusID])
REFERENCES [dbo].[Allocation_Status] ([StatusID])
GO
ALTER TABLE [dbo].[AssetDetails]  WITH CHECK ADD FOREIGN KEY([ModelID])
REFERENCES [dbo].[Models] ([ModelID])
GO
ALTER TABLE [dbo].[Employees]  WITH CHECK ADD FOREIGN KEY([DeptID])
REFERENCES [dbo].[Departments] ([DeptID])
GO
ALTER TABLE [dbo].[Models]  WITH CHECK ADD FOREIGN KEY([CatID])
REFERENCES [dbo].[Categories] ([CatID])
GO
ALTER TABLE [dbo].[Models]  WITH CHECK ADD FOREIGN KEY([ManID])
REFERENCES [dbo].[Manufacturer] ([ManID])
GO
IF NOT EXISTS (SELECT 1 FROM [dbo].[Users] WHERE [Username] = N'admin')
BEGIN
    INSERT INTO [dbo].[Users] ([Username], [Password], [Role]) VALUES (N'admin', N'admin123', N'Admin');
END
GO
USE [master]
GO
ALTER DATABASE [AssetSystem] SET  READ_WRITE 
GO
