# RU
SPmHelper — мод, который облегчит твою игру на [СПм](https://spworlds.ru).

## Зависимости мода:
1. Fabric-loader 0.16.14 или выше | [Скачать](https://fabricmc.net/use/installer/)
2. Fabric-api | [Скачать](https://modrinth.com/mod/fabric-api)
3. Cloth Config API | [Скачать](https://modrinth.com/mod/cloth-config)
4. ModMenu | [Скачать](https://modrinth.com/mod/modmenu)

## Функционал мода:
<details>
<summary> <a name="management card"> </a> 💳 Управление картами </summary>

📥 **Чтобы добавить вашу карту в мод, вам нужно:**
1. Войти на сервер СПм в майнкрафте.
2. Перейдите на [сайт](https://spworlds.ru) и зарегистрируйтесь через дискорд.
3. Перейдите во вкладку [«Кошелёк»](https://spworlds.ru/spm/wallet).
4. Выберите нужную карту и нажмите на первую эконку стрелочки «Поделиться».
5. Нажмите «Сгенерировать новый API токен» -> «Далее» -> «Сгенерировать».
6. Перейдите в игру и нажмите кнопку "Принять".

> Теперь вы добавили так свою карту в мод, можете перейти в оплату или в настройки для дальнейших действий

⚙️ **Чтобы настроить ваши карты, вам нужно:**
1. Откройте главное меню (по умолчанию на "H").
2. Перейдите в настройки.
3. Выберите доступную карту.
4. Выберите действие для карты:

   4.1 Удалить: Удаляет выбранную карту из вашего конфига. Чтобы добавить её заново, проделайте действия по добавлению снова.

   4.1 Изменить имя: Изменяет имя карты исключительно в моде. На сайте данные изменения никак не отобразятся.

   4.2 Выбрать для оплаты: Выбирает карту как основную, с которой будет производиться оплата.

</details>

<details>
<summary> <a name="payment"> </a> 💸 Оплата </summary>

⏩ **Чтобы оплатить, вам нужно:**
1. Зайти в любой мир или на любой сервер.
2. Открыть меню оплаты, по умолчанию на «P».
3. Выбрать оплату по «Никнейму» или «Карте».
4. Вбить нужные данные в поля:

   💳 **«Карта»:**

   - 4.1. Номер карты, на которую вы хотите совершить перевод.

   👤 **«Никнейм»:**
   - 4.1. Никнейм игрока.
   - 4.1.1. В правом выпадающем списке выбрать карту, на которую вы хотите перевести АРы.

   4.2. Сумма, которую вы хотите перевести. От 1 до 10000 АР.

   4.3. Комментарий. Комментарий в итоге будет содержать: `Ваш никнейм: Ваш комментарий`. Учтите, что комментарий может быть **максимум 32 символа**, с учётом длины вашего никнейма и ": ".

5. Нажать кнопку «Перевести».

</details>

<details>
<summary>🚩 Оплата по табличке </summary>

🛑 **Чтобы создать оплату по табличке, вам нужно:**
1. Установить любую табличку на сервере СПм.
2. Написать на табличке следующий текст:

   2.1. #SPmHPay | Обозначение таблички.

   2.2. 00001 | Карта, на которую будет совершён перевод.

   2.3. 64 АР | Сумма АР, от 1 до 10000. "АР" писать нужно обязательно.

   2.4. Комментарий | Что будет написано при отправке платежа.

3. Заламинировать табличку пчелиной сотой.

> **Оплата будет производится, когда вы нажимаете правой кнопкой мыши по табличке, а после подтверждаете платёж в открывшемся экране.**
</details>

<details>
<summary>⏰ Вызовы </summary>

📲 **Чтобы вызвать структуру, вам нужно:**
1. Зайти на любой сервер или мир.
2. Авторизироваться (нажать кнопку или прописать /spmhelper auth).
3. Открыть меню вызовов (по умолчанию на P).
4. Вбить следующие данные:

   4.1 Выбрать - указывать координаты или нет (указать их можно только будучи на сервере СПм).

   4.2 Написать комментарий (опционально если вы указали координаты то можно не писать комментарий).

5. Выбрать и нажать на кнопку структуры, которую вы хотите вызвать.

> За спам вызовами вы можете получить инвойс от структуры которую вы вызываете.

🛑📲 **Вызов структуры по табличке:**
1. Установить любую табличку на сервере СПм.
2. Написать на табличке следующий текст:

   2.1. #SPmHCall | Обозначение таблички.

   2.2. Детектив ; ФСБ ; Банкир ; Гид | Структура, которая будет вызвана.

   2.3. Комментарий | Что будет написано при отправке платежа.

3. Заламинировать табличку пчелиной сотой.

`При нажатии будет экран подтверждения вызова.`
> За спам вызовами вы можете получить инвойс от структуры которую вы вызываете.
</details>

<details>
<summary> <a name="nav"> </a>🚇 Навигация </summary>

🚧 **Позволяет увидеть, на какой ветке вы сейчас находитесь.**

`Берётся блок, а не ваша координата. Если вы стоите на 50.001 то отображаться будет 51.`

⚙️ **Доступные настройки:**
1. Включение / Выключение. По умолчанию включено.
2. Расположение по Y и X (высота и ширина экрана). По умолчанию: 50% X, 1% Y - сверху по-середине. 
3. Размер. По умолчанию 100%.

</details>


<details>
<summary> <a name="nav"> </a>🔐 Сканер QR кодов </summary>

🕹️ **Позволяет сканировать QR код с мап арта для дальнейшего перехода по ссылке.**

После сканирования у вас откроется экран, для перехода по ссылке которую вы отсканировали.`По умолчанию сканирование стоит на R. Можно изменить в настройках управления.`Для лучшего сканирования нажмите F1.
</details>


## Ответы на возникшие вопросы:
<details>
<summary> <a name="leave-my-money"> </a>😡 Как я могу быть уверен, что мои АРы не потратятся без моего разрешения? </summary>

> Ваши данные, а именно token и id вашей карты, хранятся исключительно на вашем компьютере в папке ./config/spmhelper и нигде больше, кроме сайта СПм.

</details>

<details>
<summary> <a name="spmhelperbot"> </a>⚙️ Я нашёл баг, куда писать? </summary>

> Напишите нашему телеграм боту для тех поддержки - https://t.me/SPmHelperBOT
> 
> Или в наш дискорд - https://discord.gg/49gTwXAqDK

</details>

<details>  
<summary> <a name="spmhelperbutton"> </a>👁️ Как убрать или изменить кнопку в меню ESC?</summary>  

> - Чтобы **убрать** кнопку из главного меню, перейдите в **Моды (ModMenu) → SPmHelper → Настройки** и измените значение поля с *"Да"* на *"Нет"*.
> - Чтобы **изменить путь**, по которому ведёт кнопка, нажмите на соответствующее поле ниже. По умолчанию настроен переход в главное меню мода.
</details>  

# EN
SPmHelper — a mod that enhances your gameplay on [SPm](https://spworlds.ru).

## Mod Dependencies:
1. Fabric-loader 0.16.14 or higher | [Download](https://fabricmc.net/use/installer/)
2. Fabric-api | [Download](https://modrinth.com/mod/fabric-api)
3. Cloth Config API | [Download](https://modrinth.com/mod/cloth-config)
4. ModMenu | [Download](https://modrinth.com/mod/modmenu)

## Mod functionality:
<details>  
<summary> <a name="management card"> </a> 💳 Card Management </summary>  

📥 **To add your card to the mod, follow these steps:**
1. Log in to an SPm Minecraft server.
2. Visit the [SPm website](https://spworlds.ru) and register via Discord.
3. Go to the [Wallet](https://spworlds.ru/spm/wallet) section.
4. Select the desired card and click the first arrow icon ("Share").
5. Click "Generate new API token" → "Next" → "Generate."
6. Return to the game and press the "Accept" button.

> Now your card is added to the mod. You can proceed to payments or settings for further actions.

⚙️ **To configure your cards:**
1. Open the main menu (default key: "H").
2. Go to Settings.
3. Select an available card.
4. Choose an action for the card:

   4.1 **Delete**: Removes the selected card from your config. To re-add it, repeat the steps above.

   4.2 **Rename**: Changes the card's name (only within the mod; no changes on the website).

   4.3 **Set as default for payments**: Marks the card as the primary one for transactions.

</details>  

<details>  
<summary> <a name="payment"> </a> 💸 Payments </summary>  

⏩ **To make a payment:**
1. Join any world or server.
2. Open the payment menu (default key: "P").
3. Choose payment by **Username** or **Card**.
4. Enter the required details:

   💳 **"Card" option:**
   - 4.1. The recipient's card number.

   👤 **"Username" option:**
   - 4.1. The player's username.
   - 4.1.1. Select the recipient's card from the dropdown menu.

   4.2. Enter the amount (1 to 10,000 AR).

   4.3. Add a comment (optional). The final comment will display as: `YourUsername: YourComment`.  
   *Note: The comment must not exceed 32 characters (including your username and ": ").*

5. Click "Send."

</details>  

<details>  
<summary>🚩 Sign-Based Payments </summary>  

🛑 **To create a payment sign:**
1. Place any sign on an SPm server.
2. Write the following text on it:

   2.1. `#SPmHPay` | Identifies the sign as a payment request.

   2.2. `00001` | The recipient's card number.

   2.3. `64 AR` | The amount (1–10,000 AR). *"AR" is mandatory.*

   2.4. `Comment` | The message attached to the payment.

3. Wax the sign with honeycomb to finalize it.

> **Payment is processed when you right-click the sign and confirm the transaction in the pop-up menu.**
</details>  

<details>  
<summary>⏰ Faction Calls </summary>  

📲 **To call a faction:**
1. Join any world or server.
2. Authenticate (press the auth button or run `/spmhelper auth`).
3. Open the faction menu (default key: "P").
4. Enter the details:

   4.1 Toggle whether to include coordinates (only works on SPm servers).

   4.2 Add a comment (optional if coordinates are provided).

5. Click the button of the faction you wish to call.

> *Excessive calls may result in an invoice from the faction.*

🛑📲 **Sign-Based Faction Calls:**
1. Place any sign on an SPm server.
2. Write:

   2.1. `#SPmHCall` | Identifies the sign as a call request.

   2.2. `Detective ; FSB ; Banker ; Guide` | The faction to call.

   2.3. `Comment` | The attached message.

3. Wax the sign with honeycomb.

`Right-clicking the sign opens a confirmation screen.`
> *Spamming calls may result in an invoice.*
</details>  

<details>  
<summary> <a name="nav"> </a>🚇 Navigation </summary>  

🚧 **Displays your current metro branch.**

*Note: The mod tracks block coordinates, not player position. Standing at 50.001 will display as 51.*

⚙️ **Settings:**
1. Toggle On/Off (default: On).
2. Adjust Y/X position (default: 50% X, 1% Y — centered top).
3. Scale size (default: 100%).

</details>  

<details>  
<summary> <a name="nav"> </a>🔐 QR Code Scanner </summary>  

🕹️ **Scans QR codes from map art for quick link access.**

After scanning, a screen opens to navigate to the link. *Default scan key: R (rebindable in controls). For better accuracy, press F1 to hide the HUD.*
</details>  

## FAQ:
<details>  
<summary> <a name="leave-my-money"> </a>😡 How can I trust that my AR won’t be spent without consent? </summary>  

> Your data (card token and ID) is stored ONLY on your computer in `./config/spmhelper` and SPm’s website—nowhere else.

</details>  

<details>  
<summary> <a name="spmhelperbot"> </a>⚙️ Where do I report bugs? </summary>  

> Contact our Telegram bot: [https://t.me/SPmHelperBOT](https://t.me/SPmHelperBOT)  
> Or join our Discord: [https://discord.gg/49gTwXAqDK](https://discord.gg/49gTwXAqDK)

</details>  

<details>  
<summary> <a name="spmhelperbutton"> </a>👁️ How do I remove/change the ESC menu button? </summary>  

> - **To remove**: Open **Mods (ModMenu) → SPmHelper → Settings** and toggle the button from *"Yes"* to *"No"*.
> - **To customize the button’s function**: Edit the target path below. Default: opens the mod’s main menu.
</details>
