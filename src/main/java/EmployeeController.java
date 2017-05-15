import java.util.List;
import spark.Request;
import spark.Response;

/**
 * Created by jaroslavtkaciuk on 01/04/2017.
 */

class EmployeeController {
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
                throw new Exception("There is no such employee");
            }
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            return employee;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
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
            return "Wrong input.";
        }

        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong input.");
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            return "No input data found!";
        }

        try {
            employeeData.create(employee, companyData);
            response.header("PATH","/employees/" + employee.getId());
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
            return "Employee successfully added id: " + employee.getId();
        } catch (Exception e){
            response.status(HTTP_UNPROCESSABLE_ENTITY);
            response.header("ERROR", "Input isn't correct");
            CompanyController.logData(request.url(), "getEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
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
            return "Wrong input.";
        }

        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong input.");
            CompanyController.logData(request.url(), "updateEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
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
            return "Employee successfully updated id: " + employee.getId();
        } catch (Exception e) {
            response.header("ERROR", "Not found!");
            response.status(HTTP_NOT_FOUND);
            CompanyController.logData(request.url(), "updateEmployee", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
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
            return "Employee with id: " + id + " successfully deleted";
        } catch (Exception e) {
            response.header("ERROR", "Not found!");
            response.status(HTTP_NOT_FOUND);
            CompanyController.logData(request.url(), "deleteEmployeeById", requestBody,
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                    CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
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
            return "No employees found";
        }
        CompanyController.logData(request.url(), "findEmployeesByName", requestBody,
                CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
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
            return "No employees found";
        }
        CompanyController.logData(request.url(), "findEmployeesByQualification", requestBody,
                CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
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
            return "No employees found";
        }
        CompanyController.logData(request.url(), "displayEmployeesByExperience", requestBody,
                CompanyController.getStringFromResponseByName(response.raw().toString(), "PATH:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "METHOD:"),
                CompanyController.getStringFromResponseByName(response.raw().toString(), "ERROR:"), response.status());
        return result;
    }

}
