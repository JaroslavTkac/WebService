import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jaroslavtkaciuk on 01/04/2017.
 */

class EmployeeData {

    private Map<Integer, Employee> employees = new HashMap();

    EmployeeData() {
        List<Employee> usersArray = Arrays.asList(
                new Employee(1, 1, "Marius", "Gumbis", "1992-02-19", 2, "Programmer"),
                new Employee(2, 1, "Povilas", "Plumbis", "1972-02-19", 3, "IT Specialist"),
                new Employee(3, 1, "Darius", "Testas", "1982-09-29", 9, "Developer"),
                new Employee(4, 2, "Stalius", "Vanagas", "1978-09-10", 5, "Programmer"),
                new Employee(5, 2, "Martinas", "Baranauskas", "1979-03-17", 1, "Programmer"),
                new Employee(6, 2, "Paulius", "Svitrigaila", "1990-01-03", 4, "Developer"),
                new Employee(7, 3, "Ignas", "Karaliaucius", "1981-04-01", 2, "Programmer"),
                new Employee(8, 3, "Laimonas", "Bajorinas", "1995-12-03", 2, "IT Specialist"),
                new Employee(9, 3, "Gestas", "Baras", "1990-09-23", 5, "Programmer"),
                new Employee(10, 3, "Stasys", "Maxima", "1994-08-30", 1, "Software Engineer"),
                new Employee(11, 4, "Laurynas", "IKI", "1976-01-31", 7, "Engineer")
        );

        usersArray.forEach(
                (employee) -> this.employees.put(employee.getId(), employee));
    }

    void create(Employee employee, CompanyData companyData) throws Exception{
        employee.setId(employees.size() + 1);
        Company company = companyData.get(employee.getCompanyId());
        if (company == null) {
            throw new Exception("There is no such company with id: " + employee.getCompanyId() + ". Can not add employee to nonexistent company.");
        }
        if(employee.getName().length() < 3 || employee.getName().equals(""))
            throw new Exception("No employee name found");
        employees.put(employee.getId(), employee);
    }

    void delete(int id) throws Exception {
        if(employees.get(id) == null)
            throw new Exception("There is no employee with id: " + id);
        employees.remove(id);
    }

    Employee get(int id) {
        return employees.get(id);
    }

    void update(int id, Employee employee, CompanyData companyData) throws Exception{
        employee.setId(id);
        Company company = companyData.get(employee.getCompanyId());
        if (company == null) {
            throw new Exception("There is no such company with id: " + employee.getCompanyId() + ". Can not add employee to nonexistent company.");
        }
        if(employees.get(id) == null)
            throw new Exception("There is no employee with id: " + id);
        employees.put(id, employee);
    }

    List<Employee> getAll() {
        return employees.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    List<Employee> findEmployeesByName(String name) {
        return employees.entrySet().stream().filter(
                (entry) -> entry.getValue().getName().toLowerCase().contains(name.toLowerCase())
        ).map( Map.Entry::getValue ).collect(Collectors.toList());
    }
    List<Employee> findEmployeesByQualification(String qualification) {
        return employees.entrySet().stream().filter(
                (entry) -> entry.getValue().getQualification().toLowerCase().contains(qualification.toLowerCase())
        ).map( Map.Entry::getValue ).collect(Collectors.toList());
    }
    List<Employee> findEmployeesByExperience(String years) {
        int exp_years = Integer.parseInt(years);
        return employees.entrySet().stream().filter(
                (entry) -> entry.getValue().getExperience() >= exp_years
        ).map( Map.Entry::getValue ).collect(Collectors.toList());
    }
}
