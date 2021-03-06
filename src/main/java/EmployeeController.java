import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

/**
 * Created by jaroslavtkaciuk on 01/04/2017.
 */

class EmployeeController {
    private static final Logger logger = LogManager.getLogger(CompanyController.class.getName());
    private static final String SEPARATOR = "************";
    private static final String NEW_LINE = "\n";

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_UNPROCESSABLE_ENTITY = 422;


    static Object getAllEmployees(Request request, Response response, EmployeeData employeeData) {
        String requestBody = request.body().replaceAll("}", "").replaceAll("\"", "")
                .replaceAll("\\{", "").replaceAll("\n", "");
    	List<Employee> result = employeeData.getAll();
        CompanyController.logData(request.url(), "getAllEmployees", requestBody,
                CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
        logInformation("getAllEmployees", Level.INFO, request, response, "Employees found " + result);
        return result;
    }

    static Object getEmployee(Request request, Response response, EmployeeData employeeData) {
        String requestBody = request.body().replaceAll("}", "").replaceAll("\"", "")
                .replaceAll("\\{", "").replaceAll("\n", "");
        try {
            int id = Integer.valueOf(request.params("id"));
            Employee employee = employeeData.get(id);
            if (employee == null) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "Not found!");
                CompanyController.logData(request.url(), "getEmployee", requestBody,
                        CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                        CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                        CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
                logInformation("getEmployee", Level.ERROR, request, response, "There is no such employee");
                throw new Exception("There is no such employee");
            }
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("getEmployee", Level.INFO, request, response, "Employee found " + employee);
            return employee;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("getEmployee", Level.ERROR, request, response, "There is no such employee with id: " + request.params("id"));
            return new ErrorMessage("There is no such employee with id: " + request.params("id"));
        }
    }

    static Object addEmployee(Request request, Response response, EmployeeData employeeData, CompanyData companyData) {
        String requestBody = request.body().replaceAll("}", "").replaceAll("\"", "")
                .replaceAll("\\{", "").replaceAll("\n", "");
        Employee employee;
        try {
            employee = JsonTransformer.fromJson(request.body(), Employee.class);
        } catch (Exception e){
            response.header("ERROR", "Wrong input.");
            response.status(HTTP_BAD_REQUEST);
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("addEmployee", Level.ERROR, request, response, "Wrong input.");
            return "Wrong input.";
        }

        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong input.");
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("addEmployee", Level.ERROR, request, response, "No input data found!");
            return "No input data found!";
        }

        try {
            employeeData.create(employee, companyData);
            response.header("PATH","/employees/" + employee.getId());
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("addEmployee", Level.INFO, request, response, "Employee successfully added id: " + employee.getId());
            return "Employee successfully added id: " + employee.getId();
        } catch (Exception e){
            response.status(HTTP_UNPROCESSABLE_ENTITY);
            response.header("ERROR", "Input isn't correct");
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("addEmployee", Level.ERROR, request, response, "Unprocessable entity");
            return new ErrorMessage(e.getMessage() + "s");
        }
    }

    static Object updateEmployee(Request request, Response response, EmployeeData employeeData, CompanyData companyData) {
        String requestBody = request.body().replaceAll("}", "").replaceAll("\"", "")
                .replaceAll("\\{", "").replaceAll("\n", "");
        Employee employee;
        try {
            employee = JsonTransformer.fromJson(request.body(), Employee.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong input.");
            CompanyController.logData(request.url(), "updateEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("updateEmployee", Level.ERROR, request, response, "Wrong input.");
            return "Wrong input.";
        }

        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong input.");
            CompanyController.logData(request.url(), "updateEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("updateEmployee", Level.ERROR, request, response, "No input data found!");
            return "No input data found!";
        }

        try {
            int id = Integer.valueOf(request.params("id"));
            employeeData.update(id, employee, companyData);
            response.header("PATH","/employees/" + employee.getId());
            CompanyController.logData(request.url(), "updateEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("updateEmployee", Level.INFO, request, response, "Employee successfully updated id: " + employee.getId());
            return "Employee successfully updated id: " + employee.getId();
        } catch (Exception e) {
            response.header("ERROR", "Not found!");
            response.status(HTTP_NOT_FOUND);
            CompanyController.logData(request.url(), "updateEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("updateEmployee", Level.ERROR, request, response, "Not found!");
            return new ErrorMessage(e.getMessage());
        }
    }

    static Object deleteEmployeeById(Request request, Response response, EmployeeData employeeData) {
        String requestBody = request.body().replaceAll("}", "").replaceAll("\"", "")
                .replaceAll("\\{", "").replaceAll("\n", "");

        try {
            int id = Integer.valueOf(request.params("id"));
            employeeData.delete(id);
            CompanyController.logData(request.url(), "deleteEmployeeById", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("deleteEmployeeById", Level.INFO, request, response, "Employee with id: " + id + " successfully deleted");
            return "Employee with id: " + id + " successfully deleted";
        } catch (Exception e) {
            response.header("ERROR", "Not found!");
            response.status(HTTP_NOT_FOUND);
            CompanyController.logData(request.url(), "deleteEmployeeById", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("deleteEmployeeById", Level.ERROR, request, response, "There is no such employee with id: " + request.params("id"));
            return new ErrorMessage("There is no such employee with id: " + request.params("id"));
        }
    }

    static Object findEmployeesByName(Request request, Response response, EmployeeData employeeData) {
        String requestBody = request.body().replaceAll("}", "").replaceAll("\"", "")
                .replaceAll("\\{", "").replaceAll("\n", "");
    	List<Employee> result = employeeData.findEmployeesByName(request.params("name"));
        if(result.isEmpty()) {
            response.header("ERROR", "Not found!");
            response.status(HTTP_NOT_FOUND);
            CompanyController.logData(request.url(), "findEmployeesByName", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("findEmployeesByName", Level.ERROR, request, response, "Not found!");
            return "No employees found";
        }
        CompanyController.logData(request.url(), "findEmployeesByName", requestBody,
                CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
        logInformation("findEmployeesByName", Level.INFO, request, response, "Emploees found: " + result);
        return result;
    }

    static Object findEmployeesByQualification(Request request, Response response, EmployeeData employeeData) {
        String requestBody = request.body().replaceAll("}", "").replaceAll("\"", "")
                .replaceAll("\\{", "").replaceAll("\n", "");
    	List<Employee> result = employeeData.findEmployeesByQualification(request.params("qualification"));
        if(result.isEmpty()) {
            response.header("ERROR", "Not found!");
            response.status(HTTP_NOT_FOUND);
            CompanyController.logData(request.url(), "findEmployeesByQualification", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("findEmployeesByQualification", Level.ERROR, request, response, "No employees found");
            return "No employees found";
        }
        CompanyController.logData(request.url(), "findEmployeesByQualification", requestBody,
                CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
        logInformation("findEmployeesByQualification", Level.INFO, request, response, "Emploees found: " + result);
        return result;
    }

    static Object displayEmployeesByExperience(Request request, Response response, EmployeeData employeeData) {
        String requestBody = request.body().replaceAll("}", "").replaceAll("\"", "")
                .replaceAll("\\{", "").replaceAll("\n", "");
    	List<Employee> result = employeeData.findEmployeesByExperience(request.params("years"));
        if(result.isEmpty()) {
            response.header("ERROR", "Not found!");
            response.status(HTTP_NOT_FOUND);
            CompanyController.logData(request.url(), "displayEmployeesByExperience", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            logInformation("displayEmployeesByExperience", Level.ERROR, request, response, "No employees found");
            return "No employees found";
        }
        CompanyController.logData(request.url(), "displayEmployeesByExperience", requestBody,
                CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
        logInformation("displayEmployeesByExperience", Level.ERROR, request, response, "Emploees found: " + result);
        return result;
    }

    private static void logInformation(String methodName, Level logLevel, Request request, Response response, Object responseData) {
        logRequest(methodName, logLevel, request);
        logResponse(methodName, logLevel, response, responseData);
    }

    private static void logRequest(String methodName, Level logLevel, Request request) {
        String information = NEW_LINE + "Request for: " + methodName + NEW_LINE + SEPARATOR + NEW_LINE;
        information = information + "request params: " + logMapValues(request.params()) + NEW_LINE + SEPARATOR + NEW_LINE;
        information = information + "request body: " + request.body() + NEW_LINE + SEPARATOR + NEW_LINE;

        if (Level.ERROR.equals(logLevel)) {
            logger.error(information);
            return;
        }

        logger.info(information);
    }

    private static String logMapValues(Map<String, String> params) {
        String result = "";
        if (params.isEmpty()) {
            result = result + "empty";
            return result;
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = result + "param key: " + entry.getKey() + ", param value: " + entry.getValue() + NEW_LINE;
        }

        return result;
    }

    private static void logResponse(String methodName, Level logLevel, Response response, Object responseData) {
        String information = NEW_LINE + "Response from: " + methodName + NEW_LINE + SEPARATOR + NEW_LINE;
        information = information + "response body: " + response.body() + NEW_LINE + SEPARATOR + NEW_LINE;
        information = information + "response status: " + response.status() + NEW_LINE + SEPARATOR + NEW_LINE;
        information = information + "response data: " + responseData + NEW_LINE + SEPARATOR + NEW_LINE;

        if (Level.ERROR.equals(logLevel)) {
            logger.error(information);
            return;
        }

        logger.info(information);
    }


}
