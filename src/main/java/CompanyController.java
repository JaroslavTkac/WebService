import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */


class CompanyController {
	private static final Logger logger = LogManager.getLogger(CompanyController.class.getName());
	private static final String SEPARATOR = "************";
	private static final String NEW_LINE = "\n";

    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_UNPROCESSABLE_ENTITY = 422;
    private static final int HTTP_SERVICE_UNAVAILABLE = 503;

    private static final String Company_Web_Service_URL = "localhost:1234";
    private static final String Bank_Web_Service_URL = "localhost:90";


    static List<Company> getAllCompanies(Request request, Response response, CompanyData companyData) {
        String account;
        Company company;
        try {
            for (int i = 1; i <= companyData.getCompanies().size(); i++) {
                account = HandleRequests.sendGETRequest("http://" + Bank_Web_Service_URL + "/accounts/" + companyData.get(i).getBankId());
                company = companyData.get(i);
                company.setBalance(getBalance(account));
                companyData.update(companyData.get(i).getCompanyId(), company);
            }
        } catch (Exception e) {
            try {
                HandleRequests.testURL("http://" + Bank_Web_Service_URL + "/accounts");
            }
            catch (Exception p){
                response.status(HTTP_SERVICE_UNAVAILABLE);
                response.header("ERROR", "Bank webservice is unavailable, actual balance cannot be shown");
                logInformation("getAllCompanies", Level.ERROR, request, response, "Bank webservice is unavailable, actual balance cannot be shown");
            }
            //response.header("ERROR", "Bank webservice is unavailable, actual balance cannot be shown");
            response.header("ERROR", "Some or all companies do not have bank account");
            logInformation("getAllCompanies", Level.ERROR, request, response, companyData.getAll());
            return companyData.getAll();
        }
        logInformation("getAllCompanies", Level.INFO, request, response, companyData.getAll());
        return companyData.getAll();
    }
	static Object getCompany(Request request, Response response, CompanyData companyData) {
        String account = "";
        try {
            int id = Integer.valueOf(request.params("id"));
            Company company = companyData.get(id);
            if (company == null) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "There is no such company with id: " + request.params("id"));
                logInformation("getCompany", Level.ERROR, request, response, "There is no such company with id: " + id);
                return "There is no such company";
            }
            try {
                account = HandleRequests.sendGETRequest("http://" + Bank_Web_Service_URL + "/accounts/" + company.getBankId());
            }
            catch (Exception e){
                try {
                    HandleRequests.testURL("http://" + Bank_Web_Service_URL + "/accounts");
                }
                catch (Exception p){
                    response.status(HTTP_SERVICE_UNAVAILABLE);
                    response.header("ERROR", "Bank webservice is unavailable");
                    logInformation("getCompany", Level.ERROR, request, response, "Bank webservice is unavailable");
                    return "Bank webservice is unavailable";
                }
                response.header("ERROR", "Company do not have bank account");
                logInformation("getCompany", Level.ERROR, request, response, "Company do not have bank account");
                return company;
            }
            company.setBalance(getBalance(account));
            logInformation("getCompany", Level.INFO, request, response, company);
            return company;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            logInformation("getCompany", Level.ERROR, request, response, "There is no such company with id: " + request.params("id"));
            return new ErrorMessage("There is no such company with id: " + request.params("id"));
        }
    }
    static Object getCompanyAccount(Request request, Response response, CompanyData companyData){
        try {
            int id = Integer.valueOf(request.params("id"));
            Company company = companyData.get(id);
            if (company == null) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "There is no such company with id: " + request.params("id"));
                logInformation("getCompanyAccount", Level.ERROR, request, response, "There is no such company with id: " + id);
                return "There is no such company";
            }
            Object account = JsonTransformer.fromJson(HandleRequests.GET("http://" + Bank_Web_Service_URL + "/accounts/" + company.getBankId())+"", Account.class);
            logInformation("getCompanyAccount", Level.INFO, request, response, "Account found!");
            return account;
        } catch (Exception e) {
            try {
                HandleRequests.testURL("http://" + Bank_Web_Service_URL + "/accounts");
            }
            catch (Exception p){
                response.status(HTTP_SERVICE_UNAVAILABLE);
                response.header("ERROR", "Bank webservice is unavailable");
                logInformation("getCompanyAccount", Level.ERROR, request, response, "Bank webservice is unavailable");
                return "Bank webservice is unavailable";
            }
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Company do not have bank account");
            logInformation("getCompanyAccount", Level.ERROR, request, response, "Company do not have bank account");
            return "Company do not have bank account";
        }
    }
    static Object getCompanyAccountTransactions(Request request, Response response, CompanyData companyData){
        try {
            int id = Integer.valueOf(request.params("id"));
            Company company = companyData.get(id);
            if (company == null) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "There is no such company with id: " + request.params("id"));
                logInformation("getCompanyAccountTransactions", Level.ERROR, request, response, "There is no such company with id: " + id);
                return "There is no such company";
            }
            if(company.getTransactionList().size() == 0) {
                response.status(HTTP_NOT_FOUND);
                response.header("ERROR", "Not found!");
                logInformation("getCompanyAccountTransactions", Level.ERROR, request, response, "No transactions found");
                return "No transactions found";
            }
            ArrayList<Object> list = new ArrayList<Object>();
            for(int i = 0; i < company.getTransactionList().size(); i++) {
                list.add(JsonTransformer.fromJson(HandleRequests.GET("http://" + Bank_Web_Service_URL + "/transactions/" + company.getTransactionList().get(i)) + "", Transaction.class));
            }
            logInformation("getCompanyAccountTransactions", Level.INFO, request, response, "Transactions found!");
            return list;
        } catch (Exception e) {
            try {
                HandleRequests.testURL("http://" + Bank_Web_Service_URL + "/transactions");
            }
            catch (Exception p){
                response.status(HTTP_SERVICE_UNAVAILABLE);
                response.header("ERROR", "Bank webservice is unavailable");
                logInformation("getCompanyAccountTransactions", Level.ERROR, request, response, "Bank webservice is unavailable");
                return "Bank webservice is unavailable";
            }
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            logInformation("getCompanyAccountTransactions", Level.ERROR, request, response, "No transactions found");
            return "No transactions found";
        }
    }
    static Object addCompany(Request request, Response response, CompanyData companyData) {
        Company company;
        Account account;
        List<String> list;
        String headerId = "";
        try {
            company = JsonTransformer.fromJson(request.body(), Company.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong Input");
            logInformation("addCompany", Level.ERROR, request, response, "Wrong input.");
            return "Wrong input.";
        }

        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong input.");
            logInformation("addCompany", Level.ERROR, request, response, "No input data found!");
            return "No input data found!";
        }

        try {
            account = new Account(0, company.getCompanyName(), company.getFounder(), company.getBalance());
            try {
                headerId = HandleRequests.POSTBankAccount("http://" + Bank_Web_Service_URL + "/accounts", account.getName(), account.getSurname(), account.getBalance());
            }catch (Exception e){
                company.setBankId(0);
                companyData.create(company);
                response.header("ERROR", "Bank webservice is unavailable");
                logInformation("addCompany", Level.ERROR, request, response, "Bank webservice is currently unavailable, company created without bank account.");
                return "Bank webservice is currently unavailable, company created without bank account.";
            }

            list = Arrays.asList(headerId.split("/"));
            company.setBankId(Integer.parseInt(list.get(2)));
            companyData.create(company);
        } catch (Exception e){
            response.status(HTTP_UNPROCESSABLE_ENTITY);
            response.header("ERROR", e.getMessage());
            logInformation("addCompany", Level.ERROR, request, response, "Unprocessable entity");
            return new ErrorMessage(e.getMessage());
        }
        response.header("PATH","/companies/" + company.getCompanyId());
        logInformation("addCompany", Level.INFO, request, response, "Company successfully added id: " + company.getCompanyId());
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
            logInformation("addCompanyTransaction", Level.ERROR, request, response, "Wrong input.");
            return "Wrong input.";
        }
        if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong input.");
            logInformation("addCompanyTransaction", Level.ERROR, request, response, "No input data found!");
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
                logInformation("addCompanyTransaction", Level.ERROR, request, response, "There is no such company with that kind of id: " + companySenderId + " or " + companyReceiverId);
                return("There is no such company with that kind of id " + companySenderId + " or " + companyReceiverId);
            }
            String headerId = "";

            try {
                HandleRequests.testURL("http://" + Bank_Web_Service_URL + "/transactions");
            }
            catch (Exception e){
                response.status(HTTP_SERVICE_UNAVAILABLE);
                response.header("ERROR", "Bank webservice is unavailable");
                logInformation("addCompanyTransaction", Level.ERROR, request, response, "Bank webservice is unavailable");
                return "Bank webservice is unavailable";
            }

            headerId = HandleRequests.POSTTransaction("http://" + Bank_Web_Service_URL + "/transactions", companySenderBankId,
                    companyReceiverBankId, (float) transaction.getAmount());

            list = Arrays.asList(headerId.split("/"));
            int id1 = Integer.parseInt(list.get(0)), id2 = Integer.parseInt(list.get(1));
            int transactionId = Integer.parseInt(list.get(3));
            companyData.getByBankId(id1).getTransactionList().add(transactionId);
            companyData.getByBankId(id2).getTransactionList().add(transactionId);
        }
        catch (Exception e){
            response.status(HTTP_UNPROCESSABLE_ENTITY);
            response.header("ERROR", e.getMessage());
            logInformation("addCompanyTransaction", Level.ERROR, request, response, "Unprocessable entity");
            return new ErrorMessage(e.getMessage());
        }
        response.header("PATH","/companies/" + companyData.get(Integer.parseInt(list.get(0))).getCompanyId() + "/account/transactions");
        logInformation("addCompanyTransaction", Level.INFO, request, response, "Company Transaction successfully completed");
        return "Company Transaction successfully completed";
    }
    static Object updateCompany(Request request, Response response, CompanyData companyData) {
        Company company;
        try {
            company = JsonTransformer.fromJson(request.body(), Company.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong Input!");
            logInformation("updateCompany", Level.ERROR, request, response, "Wrong input.");
            return "Wrong input.";
        }
        try {
            int id = Integer.valueOf(request.params("id"));

            if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
                response.status(HTTP_BAD_REQUEST);
                response.header("ERROR", "Wrong input.");
                logInformation("updateCompany", Level.ERROR, request, response, "No input data found!");
                return "No input data found!";
            }

            if(company.getCompanyName() == null || company.getCompanyName().length() < 2 || company.getCompanyName().equals("")) {
                response.status(HTTP_UNPROCESSABLE_ENTITY);
                response.header("ERROR", "Empty name");
                logInformation("updateCompany", Level.ERROR, request, response, "Please do not leave companyName field empty");
                return "Please do not leave companyName field empty";
            }
            Company curCompany = companyData.get(id);

            if(company.getBankId() == 0) {
				company.setBankId(curCompany.getBankId());
			}
            if(company.getInsureEmployees() == -1) {
				company.setInsureEmployees(curCompany.getInsureEmployees());
			}
            if(company.getReviewRating() == -1) {
				company.setReviewRating(curCompany.getReviewRating());
			}
            if(company.getFoundedAt() == null) {
				company.setFoundedAt(curCompany.getFoundedAt());
			}
            if(company.getFounder() == null) {
				company.setFounder(curCompany.getFounder());
			}
            if(company.getCity() == null) {
				company.setCity(curCompany.getCity());
			}
            if(company.getAddress() == null) {
				company.setAddress(curCompany.getAddress());
			}
            if(company.getPhoneNumber() == null) {
				company.setPhoneNumber(curCompany.getPhoneNumber());
			}
            company.transactionList = curCompany.transactionList;

            companyData.update(id, company);
            response.header("PATH","/companies/" + company.getCompanyId());
            logInformation("updateCompany", Level.INFO, request, response, "Company successfully updated id: " + company.getCompanyId());
            return "Company successfully updated id: " + company.getCompanyId();
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            logInformation("updateCompany", Level.ERROR, request, response, "Not found!");
            return new ErrorMessage(e.getMessage());
        }
    }
    static Object updateCompanyBankAccount(Request request, Response response, CompanyData companyData) {
        Account account;
        int responseCode = 0;
        try {
            account = JsonTransformer.fromJson(request.body(), Account.class);
        } catch (Exception e){
            response.status(HTTP_BAD_REQUEST);
            response.header("ERROR", "Wrong Input!");
            logInformation("updateCompanyBankAccount", Level.ERROR, request, response, "Wrong Input!");
            return "Wrong input.";
        }

        try {
            int companyId = Integer.valueOf(request.params("id"));
            int accountId = companyData.get(companyId).getBankId();

            if(request.body().trim().replaceAll("\n ", "").replaceAll(" ", "").length() <= 10) {
                response.status(HTTP_BAD_REQUEST);
                response.header("ERROR", "Wrong input.");
                logInformation("updateCompanyBankAccount", Level.ERROR, request, response, "No input data found!");
                return "No input data found!";
            }

            if(account.getName() == null || account.getName().length() < 2 || account.getName().equals("")) {
                response.status(HTTP_UNPROCESSABLE_ENTITY);
                response.header("ERROR", "Empty name");
                logInformation("updateCompanyBankAccount", Level.ERROR, request, response, "Please do not leave name field empty");
                return "Please do not leave name field empty";
            }

            if(account.getSurname() == null || account.getSurname().length() < 2 || account.getSurname().equals("")) {
                response.status(HTTP_UNPROCESSABLE_ENTITY);
                response.header("ERROR", "Empty surname");
                logInformation("updateCompanyBankAccount", Level.ERROR, request, response, "Please do not leave surname field empty");
                return "Please do not leave surname field empty";
            }
            if(!account.getName().equals(companyData.get(companyId).getCompanyName())) {
                response.status(HTTP_UNPROCESSABLE_ENTITY);
                response.header("ERROR", "Name mismatch");
                logInformation("updateCompanyBankAccount", Level.ERROR, request, response, "Account name and company name must match");
                return "Account name and company name must match";
            }
            try {
                HandleRequests.testURL("http://" + Bank_Web_Service_URL + "/accounts");
            }
            catch (Exception p){
                response.status(HTTP_SERVICE_UNAVAILABLE);
                response.header("ERROR", "Bank webservice is unavailable");
                logInformation("getCompanyAccountTransactions", Level.ERROR, request, response, "Bank webservice is unavailable");
                return "Bank webservice is unavailable";
            }
            HandleRequests.PUTBankAccount("http://" + Bank_Web_Service_URL + "/accounts/" + accountId, account.getName(), account.getSurname(), account.getBalance());
            response.header("PATH","/companies/" + companyId + "/account");
            logInformation("updateCompanyBankAccount", Level.INFO, request, response, "Company account successfully updated id: " + companyId);
            return "Company account successfully updated id: " + companyId;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            logInformation("updateCompanyBankAccount", Level.ERROR, request, response, "Not found!");
            return "Company do not have bank account to update";
        }
    }
    static Object deleteCompanyById(Request request, Response response, CompanyData companyData, EmployeeData employeeData) {
        List<Employee> list = employeeData.getAll();
        try {
            int id = Integer.valueOf(request.params("id"));
            System.out.println(companyData.get(id).getBankId());
            try {
                HandleRequests.DELETE("http://" + Bank_Web_Service_URL + "/accounts/" + companyData.get(id).getBankId());
            }catch (Exception e){
                response.header("ERROR", "Bank webservice is unavailable, bank account cannot be deleted.");
            }
            companyData.delete(id);
            for (Employee aList : list) {
                if (aList.getCompanyId() == id) {
					employeeData.delete(aList.getId());
				}
            }
            logInformation("deleteCompanyById", Level.ERROR, request, response, "Company with id: " + id + " successfully deleted.");
            return "Company with id: " + id + " successfully deleted.";
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            logInformation("deleteCompanyById", Level.ERROR, request, response, "There is no such company with id: " + request.params("id"));
            return new ErrorMessage("There is no such company with id: " + request.params("id"));
        }
    }
    static Object findCompanyByName(Request request, Response response, CompanyData companyData) {
    	List<Company> result = companyData.findByCompanyName(request.params("company_name"));
        if(result.isEmpty()) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            logInformation("findCompanyByName", Level.ERROR, request, response, "No companies found");
            return "No companies found";
        }

        logInformation("findCompanyByName", Level.INFO, request, response, "Companies found: " + result);
        return result;
    }
    static Object findCompaniesByCity(Request request, Response response, CompanyData companyData) {
    	List<Company> result = companyData.findByCompanyName(request.params("city"));
        if(result.isEmpty()) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            logInformation("findCompaniesByCity", Level.ERROR, request, response, "No companies found");
            return "No companies found";
        }

        logInformation("findCompaniesByCity", Level.INFO, request, response, "Companies found: " + result);
        return result;
    }
    static Object displayCompaniesByEmployeesQuantity(Request request, Response response, CompanyData companyData) {
    	List<Company> result = companyData.findByEmployeeQuantity(request.params("quantity"));
        if(result.isEmpty()) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            logInformation("displayCompaniesByEmployeesQuantity", Level.ERROR, request, response, "No companies found");
            return "No companies found";
        }

        logInformation("displayCompaniesByEmployeesQuantity", Level.INFO, request, response, "Companies found: " + result);
        return result;
    }
    static Object findEmployeesInCompanyById(Request request, Response response, CompanyData companyData, EmployeeData employeeData) {
        int id = Integer.valueOf(request.params("id"));
        Company company = companyData.get(id);
        if (company == null) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            logInformation("findEmployeesInCompanyById", Level.ERROR, request, response, "There is no such company");
            return "There is no such company";
        }
        try {
        	List<Employee> result = companyData.findByEmplCompanyId(request.params("id"), employeeData);
        	logInformation("findEmployeesInCompanyById", Level.INFO, request, response, "Employees found: " + result);
            return result;
        } catch (Exception e) {
            response.status(HTTP_NOT_FOUND);
            response.header("ERROR", "Not found!");
            logInformation("findEmployeesInCompanyById", Level.ERROR, request, response, "Not found!");
            return new ErrorMessage(e.getMessage());
        }

    }
    static void initWebService(){
        try {
            HandleRequests.initMyWebserviceWithPOSTMethod("http://" + Company_Web_Service_URL + "/companies",1, 1,
                    "UAB <Roklitas>",13, 6.5f,
                    "2015-02-15", "J.Jonaitis", "Vilnius","Vilniaus g. 2",
                    "roklitas@inbox.lt", "+37065692001", 19000.0f);
            HandleRequests.initMyWebserviceWithPOSTMethod("http://" + Company_Web_Service_URL + "/companies",2, 2,
                    "UAB <Plaituva>",3, 9.0f,
                    "2011-01-28", "P.Petraitis", "Vilnius","Savanoriu pr. 13",
                    "plaituva@inbox.lt", "+37065649522", 182000.0f);
            HandleRequests.initMyWebserviceWithPOSTMethod("http://" + Company_Web_Service_URL + "/companies",3, 3,
                    "UAB <MPLas>",5, 7.0f,
                    "2016-09-05", "R.Kazakevicius", "Kaunas","Saltuvos g. 37",
                    "mplas@inbox.lt", "+37065321234", 40000.0f);
            HandleRequests.initMyWebserviceWithPOSTMethod("http://" + Company_Web_Service_URL + "/companies",4, 4,
                    "UAB <Laimas>",32, 5.0f,
                    "2003-05-19", "V.Vanagas", "Klaipeda","Vytauto g. 2",
                    "laimas@admin.lt", "+37065000900", 32000.0f);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static float getBalance(String data){
        StringBuilder value = new StringBuilder();
        int k = 0;
        List<String> list = Arrays.asList(data.split(","));

        for (String aList : list) {
            if (aList.contains("\"balance\":")) {
                while (k < aList.length()) {
                    if (aList.charAt(k) == '.' || Character.isDigit(aList.charAt(k))) {
                        value.append(aList.charAt(k));
                    }
                    k++;
                }
            }
        }
        return Float.parseFloat(value.toString());
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

		for (Entry<String, String> entry : params.entrySet()) {
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
