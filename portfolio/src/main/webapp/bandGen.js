function getMood() {
    let menu = document.getElementById("mood");
    let mood = menu.options[menu.selectedIndex].text;
    return mood;
}

function generateBand() {
    const NUMBER_OF_BANDS = 2;
    let mood = getMood();
    let bands = getBands();
    let bandContainer = document.getElementById("bandContainer");
    let rand = Math.floor(Math.random() * NUMBER_OF_BANDS);
    console.log(bands);
    bandContainer.innerHTML = bands[mood][rand];
}

function getBands() {
    let bands = {};
    bands["Chill"] = ["Elijah Nang", "Joji"];
    bands["Funky"] = ["Marc Rebillet", "TWRP"];
    bands["Ecstatic"] = ["Big Bad Voodoo Daddy", "Taraf de Haidouks"];
    bands["Fancy"] = ["Devil Doll", "The Speakeasies Swing Band"];
    return bands;
}