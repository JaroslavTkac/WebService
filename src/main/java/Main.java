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

        port(1234);

        path("/companies", () -> {
            //get all companies
            get("", (Request req, Response res) -> CompanyController.getAllCompanies(req, res, companyData), new JsonTransformer());
            //get companies by id
            get("/:id", (Request req, Response res) -> CompanyController.getCompany(req, res, companyData), new JsonTransformer());
            //get companies by name
            get("/name/:company_name", (Request req, Response res) -> CompanyController.findCompanyByName(req, res, companyData), new JsonTransformer());
            //get companies by city
            get("/city/:city", (Request req, Response res) -> CompanyController.findCompaniesByCity(req, res, companyData), new JsonTransformer());
            //add Company
            post("", (Request req, Response res) -> CompanyController.addCompany(req, res, companyData), new JsonTransformer());
            //update Company
            put("/:id", (Request req, Response res) -> CompanyController.updateCompany(req, res, companyData), new JsonTransformer());
            //delete Company by id
            delete("/:id", (Request req, Response res) -> CompanyController.deleteCompanyById(req, res, companyData, employeeData), new JsonTransformer());
            //Employees
            path("/employees/" , () -> {
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
        });

        exception(Exception.class, (Exception e, Request req, Response res) -> {
            res.status(HTTP_BAD_REQUEST);
            JsonTransformer jsonTransformer = new JsonTransformer();
            res.body(jsonTransformer.render( new ErrorMessage(e) ));
        });

        after((Request req, Response rep) -> rep.type("application/json"));

    }
}