import spark.Request;
import spark.Response;

import java.util.List;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */

class CompanyController {
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_UNPROCESSABLE_ENTITY = 422;


    static Object getAllCompanies(Request request, Response response, CompanyData companyData) {
        return companyData.getAll();
    }

    static Object getCompany(Request request, Response response, CompanyData companyData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            Company company = companyData.get(id);
            if (company == null) {
                response.status(HTTP_NOT_FOUND);
                throw new Exception("There is no such company");
            }
            return company;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("There is no such company with id: " + request.params("id"));
        }
    }

    static Object addCompany(Request request, Response response, CompanyData companyData) {
        Company company;
        try {
            company = JsonTransformer.fromJson(request.body(), Company.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            return "Wrong input.";
        }

        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            return "No input data found!";
        }

        try {
            companyData.create(company);
        } catch (Exception e){
            response.status(HTTP_UNPROCESSABLE_ENTITY);
            return new ErrorMessage(e.getMessage());
        }
        return "Company successfully added id: " + company.getCompanyId();
    }

    static Object updateCompany(Request request, Response response, CompanyData companyData) {
        Company company;
        try {
            company = JsonTransformer.fromJson(request.body(), Company.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            return "Wrong input.";
        }
        try {
            if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
                response.status(HTTP_BAD_REQUEST);
                return "No input data found!";
            }

            if(company.getCompanyName() == null || company.getCompanyName().length() < 2 || company.getCompanyName().equals("")) {
                response.status(HTTP_UNPROCESSABLE_ENTITY);
                return "Please do not leave companyName field empty";
            }

            int id = Integer.valueOf(request.params("id"));
            companyData.update(id, company);
            return "Company successfully updated id: " + company.getCompanyId();
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage(e.getMessage());
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
            return "Company with id: " + id + " successfully deleted.";
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("There is no such company with id: " + request.params("id"));
        }
    }

    static Object findCompanyByName(Request request, Response response, CompanyData companyData) {
        if(companyData.findByCompanyName(request.params("company_name")).size() == 0) {
            response.status(HTTP_NOT_FOUND);
            return "No companies found";
        }
        return companyData.findByCompanyName(request.params("company_name"));
    }

    static Object findCompaniesByCity(Request request, Response response, CompanyData companyData) {
        if(companyData.findByLocation(request.params("city")).size() == 0) {
            response.status(HTTP_NOT_FOUND);
            return "No companies found";
        }
        return companyData.findByLocation(request.params("city"));
    }

    static Object displayCompaniesByEmployeesQuantity(Request request, Response response, CompanyData companyData) {
        if(companyData.findByEmployeeQuantity(request.params("quantity")).size() == 0) {
            response.status(HTTP_NOT_FOUND);
            return "No companies found";
        }
        return companyData.findByEmployeeQuantity(request.params("quantity"));
    }

    static Object findEmployeesInCompanyById(Request request, Response response, CompanyData companyData, EmployeeData employeeData) {
        int id = Integer.valueOf(request.params("id"));
        Company company = companyData.get(id);
        if (company == null) {
            response.status(HTTP_NOT_FOUND);
            return "There is no such company";
        }
        try {
            return companyData.findByEmplCompanyId(request.params("id"), employeeData);
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage(e.getMessage());
        }
    }
}
