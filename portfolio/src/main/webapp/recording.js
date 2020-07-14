function toggleShow(buttonId) {
    let id = buttonId + "Content";
    let element = document.getElementById(id);
    element.classList.toggle("hidden");
}