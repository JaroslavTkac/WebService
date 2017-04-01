# Web servisas - Imonės #

Norint paleisti servisą reikia paleisiti komandas:

`docker build -t danmerix/company:1 .`

`docker run -d -p 80:1234 danmerix/company:1`

Rest servisas bus pasiekiamas naršyklėje adresu:

`localhost:80/companies`

### Mūsų Web servise galima daryti štai ką: ###
* Peržiūrėti visas esamas imonės, kurios yra pridėtos į web serviso duombazę. 

        pvz.: request'as tipo (GET) -localhost:80/companies
* Pridėti imonę į web serviso duombazę.

        pvz.: request'as tipo (POST) - localhost:80/companies
* Atnaujinti imonę pagal nurodyta ID. 

        pvz.: request'as tipo (PUT) - localhost:80/companies/1
* Ištrinti imonę pagal nurodyta ID. 

        pvz.: request'as tipo (DELETE) - localhost:80/companies/1
* Gauti imonę pagal nurodyta ID. 

        pvz.: request'as tipo (GET) - localhost:80/companies/1
* Gauti imones pagal nurodyta pavadimą. 

        pvz.: request'as tipo (GET) - localhost:80/companies/Maxima

* Gauti imones pagal nurodyta miestą. 

        pvz.: request'as tipo (GET) - localhost:80/companies/Vilnius
* Gauti imenes pagal nurodyta apdraustu darbuotojų skaičių.

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/size/12


---


* Peržiūrėti visus esamus darbuotojus, kurie yra pridėti į web serviso duombazę. 

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/
* Pridėti darbuotoja į web serviso duombazę. 

        pvz.: request'as tipo (POST) - localhost:80/companies/employees/
* Atnaujinti darbuotoja pagal nurodyta ID. 

        pvz.: request'as tipo (PUT) - localhost:80/companies/employees/1
* Pašalinti darbuotoja pagal nurodyta ID. 

        pvz.: request'as tipo (DELETE) - localhost:80/companies/employees/1
* Gauti darbuotoja pagal nurodyta ID. 

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/1
* Gauti darbuotoja pagal nurodyta vardą. 

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/name/Mindaugas
* Gauti darbuotojus pagal nurodyta patirties lygį(metais). 

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/exp/3
* Gauti darbuotojus pagal nurodyta specialybę. 

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/qualification/Engineer



---
