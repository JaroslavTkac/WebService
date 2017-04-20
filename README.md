# Web servisas - Įmonės #

Norint paleisti servisą reikia paleisti komandas:

`docker build -t danmerix/company:2 .`

`docker run -d -p 80:1234 danmerix/company:2`

Rest servisas bus pasiekiamas naršyklėje adresu:

`localhost:80/companies`

### Mūsų Web servise galima daryti štai ką: ###
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


---


* Peržiūrėti visus esamus darbuotojus, kurie yra pridėti į web serviso duombazę. 

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/
* Pridėti darbuotoją į web serviso duombazę.

        pvz.: request'as tipo (POST) - localhost:80/companies/employees/
* Atnaujinti darbuotoją pagal nurodytą ID.

        pvz.: request'as tipo (PUT) - localhost:80/companies/employees/1
* Pašalinti darbuotoją pagal nurodytą ID.

        pvz.: request'as tipo (DELETE) - localhost:80/companies/employees/1
* Gauti darbuotoją pagal nurodytą ID.

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/1
* Gauti darbuotoją pagal nurodytą vardą.

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/name/Mindaugas
* Gauti darbuotojus pagal nurodytą patirties lygį(metais).

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/exp/3
* Gauti darbuotojus pagal nurodytą specialybę.

        pvz.: request'as tipo (GET) - localhost:80/companies/employees/qualification/Engineer



---
