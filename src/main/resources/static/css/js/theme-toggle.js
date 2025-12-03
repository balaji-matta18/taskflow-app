document.addEventListener("DOMContentLoaded", function () {
    const toggleBtn = document.getElementById("themeToggle");

    // Load saved theme
    if (localStorage.getItem("theme") === "light") {
        document.body.classList.add("light-mode");
        toggleBtn.innerHTML = "🌙";
    }

    toggleBtn.addEventListener("click", () => {
        document.body.classList.toggle("light-mode");

        // Save preference
        if (document.body.classList.contains("light-mode")) {
            localStorage.setItem("theme", "light");
            toggleBtn.innerHTML = "🌙";
        } else {
            localStorage.setItem("theme", "dark");
            toggleBtn.innerHTML = "☀️";
        }
    });
});
