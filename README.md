# ClientServerPOToGetCurrencyRates - Приложение для подучения курса валют.
Приложение для получения курса валют с сайта ЦБ РФ. Приложение на вход получает дату, за которую нужно узнать курс (или период, т.е. две даты) и название валюты (во внутреннем представлении обрабатывается код, присвоенный валюте в ЦБ), на выход выводится курс валюты. При вводе даты без указания кода валюты возвращается курс для списка валют.

Для получения курса выполняется запрос к API, чтобы получить данные с сервера и вывести их пользователю. Предусмотрено несколько типов представления полученных данных (курс для одной валюты за дату, курс для одной валюты за период, курс для списка валют за дату). Данные пользователю выводятся таблицей или графиком.

![app1](https://github.com/GrossuEvgenia/ClientServerPOToGetCurrencyRates/assets/70910919/6f9c1d91-df8c-4ea0-bcc2-994ae6d1fffb)

Рис А.1 Заполнение полей для поиска

![app2](https://github.com/GrossuEvgenia/ClientServerPOToGetCurrencyRates/assets/70910919/b9cca08e-1a3c-41fb-9802-8391543aba62)
 
Рис А.2 Вывод результата запроса конкретной валюты за дату

![app3](https://github.com/GrossuEvgenia/ClientServerPOToGetCurrencyRates/assets/70910919/12189bb5-d355-4c4e-9107-efcfb8ef2db6)

Рис А.3 Вывод результата запроса для получения списка курсов валют по дате

![app4](https://github.com/GrossuEvgenia/ClientServerPOToGetCurrencyRates/assets/70910919/ff33bc53-515d-49e9-a5c1-6ec395a8f3b4)

Рис А.4 Вывод результата запроса периода курса конкретной валюты в виде графика

![app5](https://github.com/GrossuEvgenia/ClientServerPOToGetCurrencyRates/assets/70910919/e257c16b-4cbf-41b6-abbb-90ff64952021)

Рис А.5 Вывод результата запроса периода курса конкретной валюты в виде таблицы

![app6](https://github.com/GrossuEvgenia/ClientServerPOToGetCurrencyRates/assets/70910919/aaf0512b-b6eb-4e36-8041-9de67eafacf3)

Рис А.6 Вывод сообщения об ошибке (запрашиваемые данные не были найдены)

![app7](https://github.com/GrossuEvgenia/ClientServerPOToGetCurrencyRates/assets/70910919/d7d45c53-958a-4941-b0ed-2e0b348bf868)

Рис А.7 Вывод сообщения об ошибке (некорректные параметры запроса)

