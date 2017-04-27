import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */


class CompanyController {
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_UNPROCESSABLE_ENTITY = 422;



    private static float getBalance(String data){
        String value = "";
        int k = 0;
        List<String> list = Arrays.asList(data.split(","));

        for(int i = 0; i < list.size(); i++){
            if(list.get(i).contains("\"balance\":")){
                while(k < list.get(i).length()){
                    if(list.get(i).charAt(k) == '.' || Character.isDigit(list.get(i).charAt(k))){
                        value += list.get(i).charAt(k);
                    }
                    k++;
                }
            }
        }
        return Float.parseFloat(value);
    }


    static Object getAllCompanies(Request request, Response response, CompanyData companyData) {
        //init companies balance from Bank Web Service
        String account;
        Company company;
        try {
            for (int i = 1; i <= companyData.getCompanies().size(); i++) {
                account = HandleRequests.sendGETResquest("http://bank:90/accounts/" + companyData.get(i).getCompanyId());
                company = companyData.get(i);
                company.setBalance(getBalance(account));
                companyData.update(companyData.get(i).getCompanyId(), company);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return companyData.getAll();
    }

    static Object addBankAccount(Request request, Response response, CompanyData companyData){
        Account account;
        try {
            account = JsonTransformer.fromJson(request.body(), Account.class);
            System.out.println(account.getId() + " " + account.getName() + " " +  account.getSurname()  + " " + account.getBalance());
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong Input");
            return "Wrong input.";
        }

        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong input.");
            return "No input data found!";
        }

        boolean exist = false;
        try {
            //for(int i = 0; i < companyData.getCompanies().size(); i++){
              //  if(companyData.get(i).getCompanyId() == account.getId()){
                    HandleRequests.sendPOST("http://bank:90/accounts", account.getName(), account.getSurname(), account.getBalance());
                    exist = true;
                //}
            //}
            if(!exist) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "Not found!");
                return "There is no company left without bank account";
            }
        } catch (Exception e){
            response.status(HTTP_UNPROCESSABLE_ENTITY);
            response.header("ERROR", e.getMessage());
            return new ErrorMessage(e.getMessage());
        }
        response.header("PATH","/companies/" + account.getId() + "/account");
        return "Company bank account successfully added id: " + account.getId();
    }

    static Object getAccountSummary(Request request, Response response, CompanyData companyData){
        try {
            //patiktiname ar yra tokia kompanija su companyData
            int id = Integer.valueOf(request.params("id"));
            Company company = companyData.get(id);
            if (company == null) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "There is no such company with id: " + request.params("id"));
                throw new Exception("There is no such company");
            }
            return JsonTransformer.fromJson(HandleRequests.sendGET("http://bank:90/accounts/" + id)+"", Account.class);
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            return new ErrorMessage("Company with id: " + request.params("id") + " do not have bank account :(");
        }
    }

    static Object getCompany(Request request, Response response, CompanyData companyData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            Company company = companyData.get(id);
            if (company == null) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "There is no such company with id: " + request.params("id"));
                throw new Exception("There is no such company");
            }
            return company;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            return new ErrorMessage("There is no such company with id: " + request.params("id"));
        }
    }

    static Object addCompany(Request request, Response response, CompanyData companyData) {
        Company company;
        try {
            company = JsonTransformer.fromJson(request.body(), Company.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong Input");
            return "Wrong input.";
        }

        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong input.");
            return "No input data found!";
        }

        try {
            companyData.create(company);
        } catch (Exception e){
            response.status(HTTP_UNPROCESSABLE_ENTITY);
            response.header("ERROR", e.getMessage());
            return new ErrorMessage(e.getMessage());
        }
        response.header("PATH","/companies/" + company.getCompanyId());
        return "Company successfully added id: " + company.getCompanyId();
    }

    static Object updateCompany(Request request, Response response, CompanyData companyData) {
        Company company;
        try {
            company = JsonTransformer.fromJson(request.body(), Company.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong Input!");
            return "Wrong input.";
        }
        try {
            if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
                response.status(HTTP_BAD_REQUEST);
                response.header("ERROR", "Wrong input.");
                return "No input data found!";
            }

            if(company.getCompanyName() == null || company.getCompanyName().length() < 2 || company.getCompanyName().equals("")) {
                response.status(HTTP_UNPROCESSABLE_ENTITY);
                response.header("ERROR", "Empty name");
                return "Please do not leave companyName field empty";
            }

            int id = Integer.valueOf(request.params("id"));
            companyData.update(id, company);
            response.header("PATH","/companies/" + company.getCompanyId());
            return "Company successfully updated id: " + company.getCompanyId();
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
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
            response.header("ERROR", "Not found!");
            return new ErrorMessage("There is no such company with id: " + request.params("id"));
        }
    }

    static Object findCompanyByName(Request request, Response response, CompanyData companyData) {
        if(companyData.findByCompanyName(request.params("company_name")).size() == 0) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            return "No companies found";
        }
        return companyData.findByCompanyName(request.params("company_name"));
    }

    static Object findCompaniesByCity(Request request, Response response, CompanyData companyData) {
        if(companyData.findByLocation(request.params("city")).size() == 0) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            return "No companies found";
        }
        return companyData.findByLocation(request.params("city"));
    }

    static Object displayCompaniesByEmployeesQuantity(Request request, Response response, CompanyData companyData) {
        if(companyData.findByEmployeeQuantity(request.params("quantity")).size() == 0) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            return "No companies found";
        }
        return companyData.findByEmployeeQuantity(request.params("quantity"));
    }

    static Object findEmployeesInCompanyById(Request request, Response response, CompanyData companyData, EmployeeData employeeData) {
        int id = Integer.valueOf(request.params("id"));
        Company company = companyData.get(id);
        if (company == null) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            return "There is no such company";
        }
        try {
            return companyData.findByEmplCompanyId(request.params("id"), employeeData);
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            return new ErrorMessage(e.getMessage());
        }

    }
}
