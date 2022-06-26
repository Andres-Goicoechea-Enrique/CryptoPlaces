# Proyecto Fin de Master Crypto-Places
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Aplicación Android que facilita la búsqueda y localización de estos puntos geograficos (tiendas, cafeterias, cajeros, etc) donde los clientes puedan pagar con criptomonedas. Y de este modo, cualquier persona con la aplicación, podrá consultar los establecimientos más cercanos a su ubicación actual, o si lo prefiere, también puede comprobar, a través de una serie de filtros (nombre establecimiento, categoría, país, …), si un establecimiento acepta pagos con criptomonedas.


# Herramientas, tecnologías y plataformas usadas
Esta aplicación para dispositivos Android se va a desarrollar a través del entorno de desarrollo Android Studio, haciendo uso de la plataforma Firebase para la parte de autenticación de usuarios y como gestor de bases de datos. También se utilizarán varias claves APIs de Google para mostrar en un mapa los establecimientos y generar diferentes rutas hacia ellos. Asimismo, se hará uso de la API Rest de Coinmap, la cual proporcionará todos los datos de los establecimientos que aceptan criptomonedas como forma de pago. Y por último, otra API, del exchange de criptomonedas KRAKEN, mediante la cual se mostrará el balance de criptomonedas asociado de una cuenta del exchange en la aplicación móvil.

- Android Studio Chipmunk | 2021.2.1  :shipit:
  Build #AI-212.5712.43.2112.8512546, built on April 28, 2022  
  Runtime version: 11.0.12+7-b1504.28-7817840 amd64  
  VM: OpenJDK 64-Bit Server VM by Oracle Corporation  
  
- FireBase

- API CoinMap.org (https://coinmap.org/api/)

- APIs Google (Maps SDK for Android y Directions API)
  La clave para poder usar estas APIs NO esta incluida en este repositorio, esto se debe a motivos economicos y de seguridad.
  Por lo que si se desea usar este codigo, se debe crear una API de Google en Google CLoud Platform.  
  Añadir la clave API en 'app/src/main/res/values/google_maps_api.xml'
  
  Enlace a un ejemplo para obtener dicha clave:
  https://www.youtube.com/watch?v=brCkpzAD0gc
  
  Para usar la API de Directions es necesario activar la Facturación o Billing de nuestro proyecto de Google.
  
  Enlace a un ejemplo para obtener activar la Facturación o Billing:
  https://www.youtube.com/watch?v=uINleRduCWM
  

- API privada KRAKEN 

## License and Trademarks

Copyright © 2022 Andrés Goicoechea Enrique

Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
