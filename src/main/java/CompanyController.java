import spark.Request;
import spark.Response;

import java.util.List;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */

class CompanyController {
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;

    static Object getAllCompanies(Request request, Response response, CompanyData companyData) {
        return companyData.getAll();
    }

    static Object getCompany(Request request, Response response, CompanyData companyData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            Company company = companyData.get(id);
            if (company == null) {
                throw new Exception("There is no such company");
            }
            return company;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("There is no such company with id: " + request.params("id"));
        }
    }

    static Object addCompany(Request request, Response response, CompanyData companyData) {
        Company company = JsonTransformer.fromJson(request.body(), Company.class);
        companyData.create(company);
        return "|Company successfully added to DB.| Info: " + company.toString();
    }

    static Object updateCompany(Request request, Response response, CompanyData companyData) {
        try {
            Company company = JsonTransformer.fromJson(request.body(), Company.class);
            int id = Integer.valueOf(request.params("id"));
            companyData.update(id, company);
            return "|Company successfully updated.| Info: " + company.toString();
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("There is no such company with id: " + request.params("id"));
        }
    }

    static Object deleteCompanyById(Request request, Response response, CompanyData companyData, EmployeeData employeeData) {
        List<Employee> list = employeeData.getAll();
        try {
            int id = Integer.valueOf(request.params("id"));
            companyData.delete(id);
            for (Employee aList : list) {
                if (aList.getCompanyId() == id)
                    employeeData.delete(aList.getId());
            }
            return "|Company successfully deleted.|";
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("There is no such company with id: " + request.params("id"));
        }
    }

    static Object findCompanyByName(Request request, Response response, CompanyData companyData) {
        return companyData.findByCompanyName(request.params("company_name"));
    }

    static Object findCompaniesByCity(Request request, Response response, CompanyData companyData) {
        return companyData.findByLocation(request.params("city"));
    }

    static Object displayCompaniesByEmployeesQuantity(Request request, Response response, CompanyData companyData) {
        return companyData.findByEmployeeQuantity(request.params("quantity"));
    }
}
