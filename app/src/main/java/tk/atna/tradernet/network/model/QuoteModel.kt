package tk.atna.tradernet.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuoteModel(
    //@Json(name = "c")
    val c: String, // ticker
    val ltr: String?, // last transaction rialto
    val name: String, // ticker name
    val name2: String, // latin ticker name
    val ltp: Double, // last transaction price
    val chg: Double, // price change in points
    val pcp: Double, // price change in percents
    val ltc: String?, // last transaction change label (\'\' - unchanged, \'D\' - down, \'U\' - up)
    val min_step: Double // min price step
)

/*
c	Тикер
ltr	Биржа последней сделки
name	Название бумаги
name2	Латинское название бумаги
bbp	Лучший бид
bbc	Обозначение изменения лучшего бида (\'\' - не изменился, \'D\' - вниз, \'U\' - вверх)
bbs	Количество (сайз) лучшего бида
bbf	Объем лучшего бида
bap	Лучшее предложение
bac	Обозначение изменения лучшего предложения (\'\'не изменился, \'D\'вниз, \'U\'вверх)
bas	Количество (сайз) лучшего предложения
baf	Объем лучшего предложения
pp	Цена предыдущего закрытия
op	Цена открытия в текущей торговой сессии
ltp	Цена последней сделки
lts	Количество (сайз) последней сделки
ltt	Время последней сделки
chg	Изменение цены последней сделки в пунктах относительно цены закрытия предыдущей торговой сессии
pcp	Изменение в процентах относительно цены закрытия предыдущей торговой сессии
ltc	Обозначение изменения цены последней сделки (\'\' - не изменилась, \'D\' - вниз, \'U\' - вверх)
mintp	Минимальная цена сделки за день
maxtp	Максимальная цена сделки за день
vol	Объём торгов за день в штуках
vlt	Объём торгов за день в валюте
yld	Доходность к погашению (для облигаций)
acd	Накопленный купонный доход (НКД)
fv	Номинал
mtd	Дата погашения
cpn	Купон в валюте
cpp	Купонный период (в днях)
ncd	Дата следующего купона
ncp	Дата последнего купона
dpd	ГО покупки
dps	ГО продажи
trades	Количество сделок
min_step	Минимальный шаг цены
step_price	Шаг цены
 */