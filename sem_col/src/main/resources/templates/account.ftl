<#import "common.ftl" as c>
<@c.page>
    <h1>My Account</h1>

    <div style="display: grid" >
        <span> Name Organization : ${organization.name}</span>
        <span> Country : ${organization.country.name}</span>
        <span> Currency : ${account.currency}</span>
        <span> Amount : ${account.amount}</span>
    </div>
    <form action="/account" method="post">
    <div>
        <label>Add money</label>
        <input type="number" name="amount" placeholder="amount" id="amount" style="-moz-appearance: textfield">
    </div>
    <div><input class="field" type="submit" value="add money"/></div>
    </form>

</@c.page>