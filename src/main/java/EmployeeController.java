import spark.Request;
import spark.Response;

/**
 * Created by jaroslavtkaciuk on 01/04/2017.
 */

class EmployeeController {

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;

    static Object getAllEmployees(Request request, Response response, EmployeeData employeeData) {
        return employeeData.getAll();
    }

    static Object getEmployee(Request request, Response response, EmployeeData employeeData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            Employee employee = employeeData.get(id);
            if (employee == null) {
                throw new Exception("There is no such employee");
            }
            return employee;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("There is no such employee with id: " + request.params("id"));
        }
    }

    static Object addEmployee(Request request, Response response, EmployeeData employeeData, CompanyData companyData) {
        try {
            Employee employee = JsonTransformer.fromJson(request.body(), Employee.class);
            employeeData.create(employee, companyData);
            return "|Employee successfully added to DB.| Info: " + employee.toString();
        } catch (Exception e){
            return new ErrorMessage(e.getMessage());
        }
    }

    static Object updateEmployee(Request request, Response response, EmployeeData employeeData, CompanyData companyData) {
        try {
            Employee employee = JsonTransformer.fromJson(request.body(), Employee.class);
            int id = Integer.valueOf(request.params("id"));
            employeeData.update(id, employee, companyData);
            return "|Employee successfully updated.| Info: " + employee.toString();
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            if(e.getMessage().equals("There is no such company"))
                return new ErrorMessage(e.getMessage());
            return new ErrorMessage("There is no such employee with id: " + request.params("id"));
        }
    }

    static Object deleteEmployeeById(Request request, Response response, EmployeeData employeeData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            employeeData.delete(id);
            return "|Employee successfully deleted.|";
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("There is no such employee with id: " + request.params("id"));
        }
    }

    static Object findEmployeesByName(Request request, Response response, EmployeeData employeeData) {
        return employeeData.findEmployeesByName(request.params("name"));
    }

    static Object findEmployeesByQualification(Request request, Response response, EmployeeData employeeData) {
        return employeeData.findEmployeesByQualification(request.params("qualification"));
    }

    static Object displayEmployeesByExperience(Request request, Response response, EmployeeData employeeData) {
        return employeeData.findEmployeesByExperience(request.params("years"));
    }

}
