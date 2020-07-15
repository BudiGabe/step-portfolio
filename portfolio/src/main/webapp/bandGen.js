function getMood() {
    let menu = document.getElementById("mood");
    let mood = menu.options[menu.selectedIndex].text;
    return mood;
}

