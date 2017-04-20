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
        return employeeData.getAll();
    }

    static Object getEmployee(Request request, Response response, EmployeeData employeeData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            Employee employee = employeeData.get(id);
            if (employee == null) {
                response.status(HTTP_NOT_FOUND);
                throw new Exception("There is no such employee");
            }
            return employee;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("There is no such employee with id: " + request.params("id"));
        }
    }

    static Object addEmployee(Request request, Response response, EmployeeData employeeData, CompanyData companyData) {
        Employee employee;
        try {
            employee = JsonTransformer.fromJson(request.body(), Employee.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            return "Wrong input.";
        }

        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            return "No input data found!";
        }

        try {
            employeeData.create(employee, companyData);
            return "Employee successfully added id: " + employee.getId();
        } catch (Exception e){
            response.status(HTTP_UNPROCESSABLE_ENTITY);
            return new ErrorMessage(e.getMessage());
        }
    }

    static Object updateEmployee(Request request, Response response, EmployeeData employeeData, CompanyData companyData) {
        Employee employee;
        try {
            employee = JsonTransformer.fromJson(request.body(), Employee.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            return "Wrong input.";
        }

        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            return "No input data found!";
        }

        try {
            int id = Integer.valueOf(request.params("id"));
            employeeData.update(id, employee, companyData);
            return "Employee successfully updated id: " + employee.getId();
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage(e.getMessage());
        }
    }

    static Object deleteEmployeeById(Request request, Response response, EmployeeData employeeData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            employeeData.delete(id);
            return "Employee with id: " + id + " successfully deleted";
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("There is no such employee with id: " + request.params("id"));
        }
    }

    static Object findEmployeesByName(Request request, Response response, EmployeeData employeeData) {
        if(employeeData.findEmployeesByName(request.params("name")).size() == 0) {
            response.status(HTTP_NOT_FOUND);
            return "No employees found";
        }
        return employeeData.findEmployeesByName(request.params("name"));
    }

    static Object findEmployeesByQualification(Request request, Response response, EmployeeData employeeData) {
        if(employeeData.findEmployeesByQualification(request.params("qualification")).size() == 0) {
            response.status(HTTP_NOT_FOUND);
            return "No employees found";
        }
        return employeeData.findEmployeesByQualification(request.params("qualification"));
    }

    static Object displayEmployeesByExperience(Request request, Response response, EmployeeData employeeData) {
        if(employeeData.findEmployeesByExperience(request.params("years")).size() == 0) {
            response.status(HTTP_NOT_FOUND);
            return "No employees found";
        }
        return employeeData.findEmployeesByExperience(request.params("years"));
    }

}
