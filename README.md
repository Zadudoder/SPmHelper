# RU 
SPmHelper — мод, который облегчит твою игру на [СПм](https://spworlds.ru).
## Зависимости мода:
1. Fabric-loader 0.16.14 или выше | [Скачать](https://fabricmc.net/use/installer/)
2. Fabric-api | [Скачать](https://modrinth.com/mod/fabric-api)

**По желанию можно также установить:**
1. Cloth Config API | [Скачать](https://modrinth.com/mod/cloth-config)
2. ModMenu | [Скачать](https://modrinth.com/mod/modmenu)

## Функционал мода:
<details>
<summary> <a name="payment"> </a> 🪙 Оплата </summary>

**Чтобы оплатить, вам нужно:**
1. Зайти в любой мир или на любой сервер.
2. Прописать команду /spmhelper <token> <id> | [Что такое token и id](#my-custom-anchor-point) и [как их получить](#get-token-and-id).
3. Открыть меню оплаты, по умолчанию на «P».
4. Вбить нужные данные в поля:

    4.1. Номер карты, на которую вы хотите совершить перевод.
    
    4.2. Сумма, которую вы хотите перевести. От 1 до 10000 АР.

    4.3. Комментарий. Комментарий в итоге будет содержать: `Ваш никнейм: Ваш комментарий`. Учтите, что комментарий может быть **максимум 32 символа**, с учётом длины вашего никнейма и ": ".
5. Нажать кнопку «Перевести».

![ScreenShotOfPayScreen](blob:https://yapx.ru/a20b593b-1e4c-49db-8872-17d59243ddf3)
</details>

<details>
<summary>🚩 Оплата по табличке </summary>

**Чтобы создать оплату по табличке, вам нужно:**
1. Установить любую табличку на сервере СПм
2. Написать на табличке следующий текст:

    2.1. #SPmHPay | Обозначение таблички.

    2.2. 00001 | Карта, на которую будет совершён перевод.
 
    2.3. 64 АР | Сумма АР, от 1 до 10000. "АР" писать не обязательно, можно только число.

    2.4. Комментарий | Что будет написано при отправке платежа.

3. Заламинировать табличку пчелиной сотой.

> **Оплата будет производится, когда вы нажимаете правой кнопкой мыши по табличке, а после подтверждаете платёж в открывшемся экране.**
</details>

## Ответы на возникшие вопросы:
<details>
<summary> <a name="token-and-id"> </a>❓ Что такое token и id ? </summary>

> Token и id это уникальные данные от вашей карты на СПворлдс. С помощью них мод получает доступ к вашей карте для оплаты. [Как их получить?](#get-token-and-id)

> Но если вы кому либо покажите или передадите свой токен и айди, то человек может воспользоваться этим и снять все АРы с вашей карты. [А если вы попытаетесь снять ары с моей карты?](#leave-my-money)

</details>

<details>
<summary> <a name="get-token-and-id"> </a>🫸 Как получить token и id ? </summary>

**Как получить Token и id:**
1. Войдите на сервер СПм в майнкрафте.
2. Перейдите на [сайт](https://spworlds.ru) и зарегистрируйтесь через дискорд.
3. Перейдите во вкладку [«Кошелёк»](https://spworlds.ru/spm/wallet).
4. Выберете нужную карту и нажмите на первую эконку стрелочки «Поделиться».
5. Нажмите «Сгенерировать новый API токен» -> «Далее» -> «Сгенерировать».
6. В чате игры вы увидите token и id, который уже в дальнейшем вы должны вставить в команду /spmhelper или в конфигурацию мода с помощью ModMenu.
>После успешного заполнения или выполнения команды, вам будет доступна [оплата](#payment) внутри игры.

</details>

<details>
<summary> <a name="leave-my-money"> </a>😡 Как я могу быть уверен, что мои АРы не потратятся без моего разрешения? </summary>

> Ваши данные, а именно Token и id вашей карты, хранятся исключительно на вашем компьютере в папке ./config/spmhelper и нигде больше, кроме сайта СПм.

</details>

<details>
<summary> <a name="spmhelperbot"> </a>⚙️ Я нашёл баг, куда писать? </summary>

> Напишите нашему телеграм боту для тех поддержки - https://t.me/SPmHelperBOT 

</details>


# EN
SPmHelper — a mod that will make your game easier on [SPm](https://spworlds.ru).
## Mod dependencies:
1. Fabric-loader 0.16.14 or higher | [Download](https://fabricmc.net/use/installer/)
2. Fabric-api | [Download](https://modrinth.com/mod/fabric-api)

**Optionally, you can also install:**
1. Cloth Config API | [Download](https://modrinth.com/mod/cloth-config)
2. ModMenu | [Download](https://modrinth.com/mod/modmenu)

## Mod functionality:
<details> 
<summary> <a name="payment"> </a> 🪙 Payment </summary>

**To make a payment, you need to:**
1. Enter any world or server.
2. Enter the command /spmhelper <token> <id> | [What are token and id](#my-custom-anchor-point) and [how to get them](#get-token-and-id).
3. Open the payment menu, defaulting to "P".
4. Enter the required data in the fields:

    4.1. The card number to which you want to make the transfer.

    4.2. The amount you want to transfer. From 1 to 10000 AR.

    4.3. Comment. The comment will ultimately contain: `Your nickname: Your comment`. Keep in mind that the comment can be **a maximum of 32 characters**, including the length of your nickname and ": ".
5. Click the "Transfer " button.

![ScreenShotOfPayScreen](blob:https://yapx.ru/a20b593b-1e4c-49db-8872-17d59243ddf3)
</details>

<details> 
<summary>🚩 Payment according to the table </summary>

**To create a payment based on the table, you need:**
1. Install any table on the SPb server
2. Write the following text on the sign:

2.1. #SPmHPay | Sign designation.

2.2. 00001 | Card to which the transfer will be made.

2.3. 64 AR | AR amount, from 1 to 10000. "AR" is not necessary, just the number is fine.

2.4. Comment | What will be written when sending the payment.

3. Laminate the honeycomb plaque.

Payment will be processed when you right-click on the table and then confirm the payment on the opened screen. Payment will be processed when you right-click on the table and then confirm the payment on the opened screen.
</details>

## Answers to the questions that have arisen:
<details> 
<summary> <a name="token-and-id"> </a>❓ What are token and id? </summary>

> Token and id are unique data from your card on SPworlds. With them, the mod gains access to your card for payment. [How to get them?](#get-token-and-id)

But if you show or share your token and ID with someone, that person can take advantage of it and withdraw all the ARs from your card. [What if you try to withdraw ARs from my card?](#leave-my-money)

</details>

<details> 
<summary> <a name="get-token-and-id"> </a>🫸 How to get token and id? </summary>

**How to get Token and id:**
1. Log into the SPb server in Minecraft.
2. Go to the [website](https://spworlds.ru) and register through Discord.
3. Go to the [«Wallet»](https://spworlds.ru/spm/wallet) tab.
4. Select the desired card and click on the first arrow icon "Share".
5. Click "Generate new API token" -> "Next" -> "Generate".
6. In the game chat, you will see the token and id, which you will then need to insert into the /spmhelper command or into the mod configuration using ModMenu.
> After successfully completing or executing the command, you will have access to [payment](#payment) within the game.

</details>

<details> 
<summary> <a name="leave-my-money"> </a>😡 How can I be sure that my ARs won't be spent without my permission? </summary>

Your data, specifically the Token and the ID of your card, are stored exclusively on your computer in the ./config/spmhelper folder and nowhere else, except on the SPm website.

</details>

<details> 
<summary> ⚙️ I found a bug, where should I report it? </summary>

> Write to our Telegram bot for tech support - https://t.me/SPmHelperBOT

</details>