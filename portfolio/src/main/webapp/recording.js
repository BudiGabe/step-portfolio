function toggleShow(buttonId) {
    let id = buttonId + "-content";
    let element = document.getElementById(id);
    element.classList.toggle("hidden");
}