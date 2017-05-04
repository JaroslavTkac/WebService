import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */


class CompanyController {
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_UNPROCESSABLE_ENTITY = 422;


    static List<Company> getAllCompanies(Request request, Response response, CompanyData companyData) {
        //init companies balance from Bank Web Service
        String account;
        Company company;
        try {
            for (int i = 1; i <= companyData.getCompanies().size(); i++) {
                account = HandleRequests.sendGETResquest("http://bank:1234/accounts/" + companyData.get(i).getBankId());
                company = companyData.get(i);
                company.setBalance(getBalance(account));
                companyData.update(companyData.get(i).getCompanyId(), company);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return companyData.getAll();
    }


    static Object getCompanyAccount(Request request, Response response, CompanyData companyData){
        try {
            int id = Integer.valueOf(request.params("id"));
            Company company = companyData.get(id);
            if (company == null) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "There is no such company with id: " + request.params("id"));
                throw new Exception("There is no such company");
            }
            return JsonTransformer.fromJson(HandleRequests.GET("http://bank:1234/accounts/" + company.getBankId())+"", Account.class);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    static Object getCompanyAccountTransactions(Request request, Response response, CompanyData companyData){
        try {
            int id = Integer.valueOf(request.params("id"));
            Company company = companyData.get(id);
            if (company == null) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "There is no such company with id: " + request.params("id"));
                throw new Exception("There is no such company");
            }
            if(company.getTransactionList().size() == 0) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "Not found!");
                return "No transactions found";
            }
            ArrayList<Object> list = new ArrayList<Object>();
            for(int i = 0; i < company.getTransactionList().size(); i++) {
                list.add(JsonTransformer.fromJson(HandleRequests.GET("http://bank:1234/transactions/" + company.getTransactionList().get(i)) + "", Transaction.class));
            }
            return list;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    static Object getCompany(Request request, Response response, CompanyData companyData) {
        String account;
        try {
            int id = Integer.valueOf(request.params("id"));
            Company company = companyData.get(id);
            if (company == null) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "There is no such company with id: " + request.params("id"));
                throw new Exception("There is no such company");
            }
            account = HandleRequests.sendGETResquest("http://bank:1234/accounts/" + company.getBankId());
            company.setBalance(getBalance(account));
            return company;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            return new ErrorMessage("There is no such company with id: " + request.params("id"));
        }
    }

    static Object addCompany(Request request, Response response, CompanyData companyData) {
        Company company;
        Account account;
        List<String> list;
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
            account = new Account(0, company.getCompanyName(), company.getFounder(), company.getBalance());
            String headerId = HandleRequests.POSTBankAccount("http://bank:1234/accounts", account.getName(), account.getSurname(), account.getBalance());
            list = Arrays.asList(headerId.split("/"));
            company.setBankId(Integer.parseInt(list.get(2)));
            companyData.create(company);
        } catch (Exception e){
            response.status(HTTP_UNPROCESSABLE_ENTITY);
            response.header("ERROR", e.getMessage());
            return new ErrorMessage(e.getMessage());
        }
        response.header("PATH","/companies/" + company.getCompanyId());
        return "Company successfully added id: " + company.getCompanyId();
    }

    static Object addCompanyTransaction(Request request, Response response, CompanyData companyData){
        List<String> list;
        Transaction transaction;
        try {
            transaction = JsonTransformer.fromJson(request.body(), Transaction.class);
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
        try{
            int companySenderId = transaction.getSenderId();
            int companyReceiverId = transaction.getReceiverId();
            int companySenderBankId = 0;
            int companyReceiverBankId = 0;
            if(companyData.get(companySenderId) != null && companyData.get(companyReceiverId) != null){
                companySenderBankId = companyData.get(companySenderId).getBankId();
                companyReceiverBankId = companyData.get(companyReceiverId).getBankId();
            }
            else{
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "There is no such company with that kind of id: " + companySenderId + " or " + companyReceiverId);
                return("There is no such company with that kind of id " + companySenderId + " or " + companyReceiverId);
            }

            String headerId = HandleRequests.POSTTracnsaction("http://bank:1234/transactions", companySenderBankId,
                    companyReceiverBankId, (float)transaction.getAmount());
            list = Arrays.asList(headerId.split("/"));

            int id1 = Integer.parseInt(list.get(0)), id2 = Integer.parseInt(list.get(1));

            if(companyData.getByBankId(id1) != null && companyData.getByBankId(id2) != null){
                int transactionId = Integer.parseInt(list.get(3));
                companyData.getByBankId(id1).getTransactionList().add(transactionId);
                companyData.getByBankId(id2).getTransactionList().add(transactionId);
            }
            else{
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "There is no such company with that kind of account id: " + list.get(0) + " or " + list.get(1));
                return("There is no such company with that kind of account id");
            }
        }
        catch (Exception e){
            response.status(HTTP_UNPROCESSABLE_ENTITY);
            response.header("ERROR", e.getMessage());
            return new ErrorMessage(e.getMessage());
        }
        response.header("PATH","/companies/" + companyData.get(Integer.parseInt(list.get(0))).getCompanyId() + "/account/transactions");
        return "Company Transaction successfully completed";
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
    static Object updateCompanyBankAccount(Request request, Response response, CompanyData companyData) {
        Account account;
        try {
            account = JsonTransformer.fromJson(request.body(), Account.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong Input!");
            return "Wrong input.";
        }

        try {
            int companyId = Integer.valueOf(request.params("id"));
            int accountId = companyData.get(companyId).getBankId();

            if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
                response.status(HTTP_BAD_REQUEST);
                response.header("ERROR", "Wrong input.");
                return "No input data found!";
            }

            if(account.getName() == null || account.getName().length() < 2 || account.getName().equals("")) {
                response.status(HTTP_UNPROCESSABLE_ENTITY);
                response.header("ERROR", "Empty name");
                return "Please do not leave name field empty";
            }

            if(account.getSurname() == null || account.getSurname().length() < 2 || account.getSurname().equals("")) {
                response.status(HTTP_UNPROCESSABLE_ENTITY);
                response.header("ERROR", "Empty surname");
                return "Please do not leave surname field empty";
            }
            if(!account.getName().equals(companyData.get(companyId).getCompanyName())) {
                response.status(HTTP_UNPROCESSABLE_ENTITY);
                response.header("ERROR", "Name mismatch");
                return "Account name and company name must match";
            }

            HandleRequests.PUTBankAccount("http://localhost:90/accounts/" + accountId, account.getName(), account.getSurname(), account.getBalance());
            response.header("PATH","/companies/" + companyId + "/account");
            return "Company account successfully updated id: " + companyId;
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
            System.out.println(companyData.get(id).getBankId());
            HandleRequests.DELETE("http://localhost:90/accounts/" + companyData.get(id).getBankId());
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

    static void initWebService(){
        try {
            HandleRequests.initMyWebserviceWithPOSTMethod("http://company:80/companies",1, 1,
                    "UAB <Roklitas>",13, 6.5f,
                    "2015-02-15", "J.Jonaitis", "Vilnius","Vilniaus g. 2",
                    "roklitas@inbox.lt", "+37065692001", 19000.0f);
            HandleRequests.initMyWebserviceWithPOSTMethod("http://company:80/companies",2, 2,
                    "UAB <Plaituva>",3, 9.0f,
                    "2011-01-28", "P.Petraitis", "Vilnius","Savanoriu pr. 13",
                    "plaituva@inbox.lt", "+37065649522", 182000.0f);
            HandleRequests.initMyWebserviceWithPOSTMethod("http://company:80/companies",3, 3,
                    "UAB <MPLas>",5, 7.0f,
                    "2016-09-05", "R.Kazakevicius", "Kaunas","Saltuvos g. 37",
                    "mplas@inbox.lt", "+37065321234", 40000.0f);
            HandleRequests.initMyWebserviceWithPOSTMethod("http://company:80/companies",4, 4,
                    "UAB <Laimas>",32, 5.0f,
                    "2003-05-19", "V.Vanagas", "Klaipeda","Vytauto g. 2",
                    "laimas@admin.lt", "+37065000900", 32000.0f);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
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
}
