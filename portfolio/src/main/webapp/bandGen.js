function getMood() {
    let menu = document.getElementById("mood");
    let mood = menu.options[menu.selectedIndex].text;
    return mood;
}

function generateBand() {
    const NUMBER_OF_BANDS = 2;
    let mood = getMood();
    let bands = addBands();
    let bandContainer = document.getElementById("bandContainer");
    let rand = Math.floor(Math.random() * NUMBER_OF_BANDS);
    bandContainer.innerHTML = band[mood][rand];
}

function getBands() {
    let bands = {};
    band["Chill"] = ["Elijah Nang", "Joji"];
    band["Funky"] = ["Marc Rebillet", "TWRP"];
    band["Ecstatic"] = ["Big Bad Voodoo Daddy", "Taraf de Haidouks"];
    band["Fancy"] = ["Devil Doll", "The Speakeasies Swing Band"];
    return band;
}