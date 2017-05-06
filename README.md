# Web servisas - Įmonės #

Norint paleisti servisą reikia paleisti komandas:

`docker build -t danmerix/company:3 .`

`docker run -d -p 80:1234 danmerix/company:3`

Rest servisas bus pasiekiamas naršyklėje adresu:

`localhost:80/companies`

### Mūsų Web servise galima daryti štai ką: ###



---
Naujas funkcionalas:

kaip paleisti? 

        Atsiusti docker-compose.yml ir parašyti komandą: docker-compose up -d

* Kompanijos paduoda užklausą į banką ir gauna aktulu balansą.
    
        (GET) localhost:80/companies
* Gaunam kompanijos banko sąskaita.
    
        (GET) localhost:80/companies/1/account
* Gaunam pasirinktos kompanijos banko sąskaitos visas transakcijas.
    
        (GET) localhost:80/companies/1/account/transactions
* Pridėti įmonę į web serviso duombazę, automatiškai susikuria banko sąskaita

        (POST) - localhost:80/companies
* Atnaujinti kompanijos banko sąskaita.
         
        (PUT) localhost:80/companies/1/account
        pavyzdys:
        {
            "name": "UAB <Roklitas>",
            "surname": "J.Jonaitis",
            "balance": 100
        }
* Įvykdom pinigų transakciją -> siunčiam pinigus naudojantis banko web servisu.
         
        (POST) localhost:80/companies/transactions
        pavyzdys:
        {
            "senderId": 1,
            "receiverId": 2,
            "amount": 10
        }
       
---


* Peržiūrėti visas esamas įmones, kurios yra pridėtos į web serviso duombazę.

        pvz.: request'as tipo (GET) -localhost:80/companies
* Pridėti įmonę į web serviso duombazę.

        pvz.: request'as tipo (POST) - localhost:80/companies
* Atnaujinti įmonę pagal nurodytą ID.

        pvz.: request'as tipo (PUT) - localhost:80/companies/1
* Ištrinti įmonę pagal nurodytą ID.

        pvz.: request'as tipo (DELETE) - localhost:80/companies/1
* Gauti įmonę pagal nurodytą ID.

        pvz.: request'as tipo (GET) - localhost:80/companies/1
* Gauti įmones pagal nurodytą pavadimą.

        pvz.: request'as tipo (GET) - localhost:80/companies/name/Maxima

* Gauti įmones pagal nurodytą miestą.

        pvz.: request'as tipo (GET) - localhost:80/companies/city/Vilnius
* Gauti įmones pagal nurodytą apdraustų darbuotojų skaičių.

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/size/12

* Gauti darbuotojus kurie yra priregistruoti prie įmones su nurodytu ID.

        pvz.: request'as tipo (GET) - localhost:80/companies/1/employees

---


* Peržiūrėti visus esamus darbuotojus, kurie yra pridėti į web serviso duombazę. 

        pvz.: request'as tipo (GET) - localhost:80/employees
* Pridėti darbuotoją į web serviso duombazę.

        pvz.: request'as tipo (POST) - localhost:80/employees/
* Atnaujinti darbuotoją pagal nurodytą ID.

        pvz.: request'as tipo (PUT) - localhost:80/employees/1
* Pašalinti darbuotoją pagal nurodytą ID.

        pvz.: request'as tipo (DELETE) - localhost:80/employees/1
* Gauti darbuotoją pagal nurodytą ID.

        pvz.: request'as tipo (GET) - localhost:80/employees/1
* Gauti darbuotoją pagal nurodytą vardą.

        pvz.: request'as tipo (GET) - localhost:80/employees/name/Mindaugas
* Gauti darbuotojus pagal nurodytą patirties lygį(metais).

        pvz.: request'as tipo (GET) - localhost:80/employees/exp/3
* Gauti darbuotojus pagal nurodytą specialybę.

        pvz.: request'as tipo (GET) - localhost:80/employees/qualification/Engineer



---
