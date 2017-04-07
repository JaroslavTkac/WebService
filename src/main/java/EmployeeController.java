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
        Employee employee;
        try {
            employee = JsonTransformer.fromJson(request.body(), Employee.class);
        } catch (Exception e){
            return "Wrong input.";
        }
        try {
            if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10)
                return "No input data found!";
            employeeData.create(employee, companyData);
            return "|Employee successfully added to DB.| Info: " + employee.toString();
        } catch (Exception e){
            return new ErrorMessage(e.getMessage());
        }
    }

    static Object updateEmployee(Request request, Response response, EmployeeData employeeData, CompanyData companyData) {
        Employee employee;
        try {
            employee = JsonTransformer.fromJson(request.body(), Employee.class);
        } catch (Exception e){
            return "Wrong input.";
        }
        try {
            int id = Integer.valueOf(request.params("id"));
            if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10)
                return "No input data found!";
            if(!(employee.getName() == null))
                return "Need at least employee name";
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
        if(employeeData.findEmployeesByName(request.params("name")).size() == 0)
            return "No employees found";
        return employeeData.findEmployeesByName(request.params("name"));
    }

    static Object findEmployeesByQualification(Request request, Response response, EmployeeData employeeData) {
        if(employeeData.findEmployeesByQualification(request.params("qualification")).size() == 0)
            return "No employees found";
        return employeeData.findEmployeesByQualification(request.params("qualification"));
    }

    static Object displayEmployeesByExperience(Request request, Response response, EmployeeData employeeData) {
        if(employeeData.findEmployeesByExperience(request.params("years")).size() == 0)
            return "No employees found";
        return employeeData.findEmployeesByExperience(request.params("years"));
    }

}
