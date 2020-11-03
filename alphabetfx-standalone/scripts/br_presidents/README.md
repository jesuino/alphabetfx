### How to get Image URLs:

Went to wikipedia: https://pt.wikipedia.org/wiki/Lista_de_presidentes_do_Brasil

Then run:
```
[].forEach.call(document.querySelectorAll("#mw-content-text > div.mw-parser-output > table.wikitable > tbody > tr"), tr => {
    const n = tr.querySelector("td:nth-child(1) > b");
    const name = tr.querySelector("td:nth-child(2)");
    const img = tr.querySelector("td:nth-child(3) > a > img");
    const img2 = tr.querySelector("td:nth-child(3) > b > a > img");
    if (n && n.innerHTML != "—") {
        const sr = img ? img.src : img2.src;  
        console.log(`${n.textContent},${name.textContent},${sr}`);
    }

});
``` 
