function getMood() {
    let menu = document.getElementById("mood");
    let mood = menu.options[menu.selectedIndex].text;
    return mood;
}

function generateBand() {
    let mood = getMood();
    let band = [];
    band["Chill"] = ["Elijah Nang", "Joji"];
    band["Funky"] = ["Marc Rebillet", "TWRP"];
    band["Ecstatic"] = ["Big Bad Voodoo Daddy", "Taraf de Haidouks"];
    band["Fancy"] = ["Devil Doll", "The Speakeasies Swing Band"];
    let bandContainer = document.getElementById("bandContainer");
    let rand = Math.floor(Math.random() * Math.floor(2));
    bandContainer.innerHTML = band[mood][rand];
}