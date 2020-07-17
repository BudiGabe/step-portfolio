function getMood() {
    let menu = document.getElementById("mood");
    let mood = menu.options[menu.selectedIndex].text;
    return mood;
}

function generateBand() {
    const numberOfBands = 5;
    let mood = getMood();
    let bands = getBands();
    let bandContainer = document.getElementById("bandContainer");
    let rand = Math.floor(Math.random() * numberOfBands);
    bandContainer.innerHTML = bands[mood][rand];
}

function getBands() {
    let bands = {};
    bands["Chill"] = ["Elijah Nang", "Joji", "Syros", "DopeSmoker",
        "Mystic Sons"];
    bands["Funky"] = ["Marc Rebillet", "TWRP", "Ninja Sex Party", 
        "Flamingosis", "Too Many Zooz"];
    bands["Ecstatic"] = ["Big Bad Voodoo Daddy", "Taraf de Haidouks",
        "Parov Stelar", "The Dreadnoughts", "DJ Blyatman"];
    bands["Fancy"] = ["Devil Doll", "The Speakeasies Swing Band", 
        "Postmodern Jukebox", "Destiny Quartet", "BGKO"];
    bands["Shoot a cowboy"] = ["Blues Saraceno", "Nick Nolan", "William Elliot Whitmore",
        "Justin Johnson", "Kenneth Sorenson"];
    bands["Go to war"] = ["Skald", "Wardruna", "Danheim", "Percival", "The HU"];
    bands["Sacrifice blood for the Blood God"] = 
        ["Sunn O)))", "Heilung", "Igorrr", "Black Mountain Transmitter", "Electric Wizard"];
    return bands;
}