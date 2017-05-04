import spark.Request;
import spark.Response;


import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static spark.Spark.*;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */

public class Main {
    public static void main(String[] args) {
        CompanyData companyData = new CompanyData();
        EmployeeData employeeData = new EmployeeData();


        port(80);

        path("/companies", () -> {

            //What's new here:
            //--------
            //get all companies (automatically LOADS all latest bank account balance from Bank web service)
            get("", (Request req, Response res) -> CompanyController.getAllCompanies(req, res, companyData), new JsonTransformer());
            //get company account (automatically LOADS all latest bank account balance from Bank web service)
            get("/:id/account", (Request req, Response res) -> CompanyController.getCompanyAccount(req, res, companyData), new JsonTransformer());
            //add Company (automatically CREATES bank account in Bank web service)
            post("", (Request req, Response res) -> CompanyController.addCompany(req, res, companyData), new JsonTransformer());
            //delete Company by id (automatically DELETES bank account in Bank web service)
            delete("/:id", (Request req, Response res) -> CompanyController.deleteCompanyById(req, res, companyData, employeeData), new JsonTransformer());
            //update Company bank account
            put("/:id/account", (Request req, Response res) -> CompanyController.updateCompanyBankAccount(req, res, companyData), new JsonTransformer());

            //add Transaction
            post("/transactions", (Request req, Response res) -> CompanyController.addCompanyTransaction(req, res, companyData), new JsonTransformer());
            //get all Companies Transactions
            get("/:id/account/transactions", (Request req, Response res) -> CompanyController.getCompanyAccountTransactions(req, res, companyData), new JsonTransformer());
            //--------


            //get companies by id
            get("/:id", (Request req, Response res) -> CompanyController.getCompany(req, res, companyData), new JsonTransformer());
            //get companies by name
            get("/name/:company_name", (Request req, Response res) -> CompanyController.findCompanyByName(req, res, companyData), new JsonTransformer());
            //get companies by city
            get("/city/:city", (Request req, Response res) -> CompanyController.findCompaniesByCity(req, res, companyData), new JsonTransformer());
            //get employees form company
            get("/:id/employees", (Request req, Response res) -> CompanyController.findEmployeesInCompanyById(req, res, companyData, employeeData), new JsonTransformer());
            //update Company
            put("/:id", (Request req, Response res) -> CompanyController.updateCompany(req, res, companyData), new JsonTransformer());
        });
        //Employees
        path("/employees" , () -> {
            //get all employees
            get("", (Request req, Response res) -> EmployeeController.getAllEmployees(req, res, employeeData), new JsonTransformer());
            //get all companies with >= employee size
            get("/size/:quantity", (Request req, Response res) -> CompanyController.displayCompaniesByEmployeesQuantity(req, res, companyData), new JsonTransformer());
            //get employee by id
            get("/:id", (Request req, Response res) -> EmployeeController.getEmployee(req, res, employeeData), new JsonTransformer());
            //get employees by qualification
            get("/qualification/:qualification", (Request req, Response res) -> EmployeeController.findEmployeesByQualification(req, res, employeeData), new JsonTransformer());
            //get employees by name
            get("/name/:name", (Request req, Response res) -> EmployeeController.findEmployeesByName(req, res, employeeData), new JsonTransformer());
            //get employees by experience
            get("/exp/:years", (Request req, Response res) -> EmployeeController.displayEmployeesByExperience(req, res, employeeData), new JsonTransformer());
            //add employee
            post("", (Request req, Response res) -> EmployeeController.addEmployee(req, res, employeeData, companyData), new JsonTransformer());
            //update employee
            put("/:id", (Request req, Response res) -> EmployeeController.updateEmployee(req, res, employeeData, companyData), new JsonTransformer());
            //delete employee by id
            delete("/:id", (Request req, Response res) -> EmployeeController.deleteEmployeeById(req, res, employeeData), new JsonTransformer());
        });

        exception(Exception.class, (Exception e, Request req, Response res) -> {
            res.status(HTTP_BAD_REQUEST);
            JsonTransformer jsonTransformer = new JsonTransformer();
            res.body(jsonTransformer.render( new ErrorMessage(e) ));
        });

        after((Request req, Response rep) -> rep.type("application/json"));

        //init companies and bank account
        try {
            Thread.sleep(1000);
            CompanyController.initWebService();
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}