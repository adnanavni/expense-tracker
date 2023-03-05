CREATE TABLE Registration
(
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(200) NOT NULL,
  password VARCHAR(200) NOT NULL,
  currency VARCHAR(200) NOT NULL DEFAULT 'EUR',
  ThemeColor VARCHAR(200) NOT NULL DEFAULT '#85bb65',
  PRIMARY KEY (id)
);

CREATE TABLE Incomes
(
  IncomeID INT NOT NULL AUTO_INCREMENT,
  Type VARCHAR(200) NOT NULL,
  Amount DOUBLE NOT NULL,
  Amount_minus_Taxes DOUBLE NOT NULL,
  Currency VARCHAR(200) NOT NULL DEFAULT 'EUR',
  SalaryDate DATE NOT NULL,
  TaxRate DOUBLE NOT NULL,
  UserID INT NOT NULL,
  PRIMARY KEY (IncomeID),
  FOREIGN KEY (UserID) REFERENCES Registration(id)
);

CREATE TABLE Constantexpenses
(
  ConstanexpenseId INT NOT NULL AUTO_INCREMENT,
  Title VARCHAR(200) NOT NULL,
  Amount DOUBLE NOT NULL,
  registration_id INT NOT NULL,
  PRIMARY KEY (ConstanexpenseId),
  FOREIGN KEY (registration_id) REFERENCES Registration(id)
);

CREATE TABLE Budgets
(
  BudgetId INT NOT NULL AUTO_INCREMENT,
  BudgetName VARCHAR(200) NOT NULL,
  Money DOUBLE NOT NULL,
  registration_id INT NOT NULL,
  PRIMARY KEY (BudgetId),
  FOREIGN KEY (registration_id) REFERENCES Registration(id)
);

CREATE TABLE Expenses
(
  ExpenseId INT NOT NULL AUTO_INCREMENT,
  ExpenseType VARCHAR(200) NOT NULL,
  Money DOUBLE NOT NULL,
  BudgetId INT NOT NULL,
  ExpenseDate INT NOT NULL,
  PRIMARY KEY (ExpenseId),
  FOREIGN KEY (BudgetId) REFERENCES Budgets(BudgetId)
);