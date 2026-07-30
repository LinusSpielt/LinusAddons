<h1 align="center">
  <img src="src/main/resources/assets/iq/icon.png" alt="IQ Logo" width="100"><br>
  LinusAddons
</h1>

<div align="center">
  <b>A Hypixel SkyBlock mod for Kuudra</b><br>
  <sub>aimed at improving existing features and adding new stuff</sub>
</div>

<br>

<div align="center">

[![Discord](https://img.shields.io/badge/Discord-Join-5865F2?logo=discord&logoColor=black)](https://discord.gg/fJeDhZbv2a)
[![Fabric](https://img.shields.io/badge/Fabric-26.1.2-yellow?logo=data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBzdGFuZGFsb25lPSJubyI/Pgo8IURPQ1RZUEUgc3ZnIFBVQkxJQyAiLS8vVzNDLy9EVEQgU1ZHIDIwMDEwOTA0Ly9FTiIKICJodHRwOi8vd3d3LnczLm9yZy9UUi8yMDAxL1JFQy1TVkctMjAwMTA5MDQvRFREL3N2ZzEwLmR0ZCI+CjxzdmcgdmVyc2lvbj0iMS4wIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciCiB3aWR0aD0iMjguMDAwMDAwcHQiIGhlaWdodD0iMjguMDAwMDAwcHQiIHZpZXdCb3g9IjAgMCAyOC4wMDAwMDAgMjguMDAwMDAwIgogcHJlc2VydmVBc3BlY3RSYXRpbz0ieE1pZFlNaWQgbWVldCI+Cgo8ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgwLjAwMDAwMCwyOC4wMDAwMDApIHNjYWxlKDAuMTAwMDAwLC0wLjEwMDAwMCkiCmZpbGw9IiMwMDAwMDAiIHN0cm9rZT0ibm9uZSI+CjxwYXRoIGQ9Ik0xNDMgMjY1IGMtNyAtOCAtMTMgLTE5IC0xMyAtMjUgMCAtNiAtMjcgLTM4IC02MCAtNzAgLTMzIC0zMiAtNjAKLTY0IC02MCAtNzAgMCAtMTQgODYgLTEwMCAxMDAgLTEwMCAxNSAwIDQwIDI1IDQwIDQwIDAgMTQgNjYgODAgODAgODAgMTUgMAo0MCAyNSA0MCA0MCAwIDkgLTEwNSAxMjEgLTExMiAxMjAgLTIgLTEgLTkgLTcgLTE1IC0xNXogbTUyIC00MCBjMzAgLTMwIDQ2Ci02NSAzMCAtNjUgLTEzIDAgLTc1IDcxIC03NSA4NSAwIDIyIDcgMTkgNDUgLTIweiBtLTIwIC00MCBjMTkgLTE5IDM1IC0zOSAzNQotNDUgMCAtNiAtMjUgLTM1IC01NSAtNjUgbC01NSAtNTYgLTM1IDM1IGMtMTkgMjAgLTM1IDQwIC0zNSA0NiAwIDggOTggMTE3CjEwOCAxMTkgMSAxIDE4IC0xNSAzNyAtMzR6Ii8+CjwvZz4KPC9zdmc+Cg==)](https://fabricmc.net)
[![License](https://img.shields.io/badge/License-MIT-28A745?logo=apache&logoColor=black)](https://github.com/LinusSpielt/LinusAddons/blob/main/LICENSE)

</div>

---

## ✨ Features

### General


<details>
<summary><b>Rend & Hollow Fix</b></summary>
<br>
Aims to fix an issue in the way Rend and Hollow leftclicks are computed by the server when using them on an entity.

- blocks voiding attack packets
- creates the correct attack packet and sends it
</details>

<details>
<summary><b>Auto GFS</b></summary>
<br>
Automatically refills from your Sacks.

- refills pearls when below a threshhold
- gets a chosen ammount of wap/twap before DPS phase starts
</details>

<details>
<summary><b>Pickobulus Blocker</b></summary>
<br>
Prevents you from accidentally using the Pickaxe ability too early.
</details>

### Phase 1 — Supplies

<details>
<summary><b>Dynamic Waypoints</b></summary>
<br>
Renders custom Waypoints depending on what tentacles exist in the current Bossfight.

- Create custom Waypoints
- Set conditions under which they should render
- Fully customizable via JSON config
</details>

<details>
<summary><b>Tentacle Highlight</b></summary>
<br>
Draws colored Boxes around spawning Tentacles depending on the size.
</details>

<details>
<summary><b>Supply Rod Circle</b></summary>
<br>
Draws a circle around the Supply that the rod interacts with the supply in.
</details>

---

### Phase 2 — Build

<details>
<summary><b>Hide Rend Cooldown</b></summary>
<br>
Stops the rend cooldown message from appearing during Build Phase.
</details>

<details>
<summary><b>Build Progress Widget</b></summary>
<br>
Widget to keep track of current Build Progress.

- LIVE calculation
- More accurate than other Progress Trackers
</details>

<details>
<summary><b>Build Start Widget</b></summary>
<br>
Displays a countdown till the placed crates are interactable with.
</details>

---

### Phase 3 — Stun

<details>
<summary><b>Stun Waypoint & Tracer</b></summary>
<br>
Displays a waypoint at the correct stun location as well as a tracer to it.
</details>

<details>
<summary><b>DPS Waypoints</b></summary>
<br>
Renders Waypoints for top and bottom DPS spots during Stun Phase.
</details>

---

### Phase 4 — Boss Fight

<details>
<summary><b>Better Direction Alert</b></summary>
<br>
Shows which direction Kuudra will spawn from.

- About 3-5 ticks faster 
- Uses entity rotation instead of position for early detection
</details>

<details>
<summary><b>Boss Block Waypoint</b></summary>
<br>
Renders a Waypoint for the missplaced block on the right side.
</details>

<details>
<summary><b>Bone Aim Waypoint</b></summary>
<br>
Renders a dynamic Waypoint for the perfect spot to throw your bone at.
</details>

---

## Dynamic Waypoints Customization

Edit your dynamic Waypoints in `dynamic_waypoints.json` to customize positions and render conditions. The mod will automatically reload changes.

---

## 💬 Support

Found a bug or have suggestions?

- **Bug Reports:** Open an [issue on GitHub](https://github.com/LinusSpielt/LinusAddons/issues)
- **Feature Requests:** Join the [Discord](https://discord.gg/fJeDhZbv2a)

---

## 📜 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👥 Credits

<table>
  <tr>
    <td align="center"><b>Linus_Spielt</b><br><sub>Developer</sub></td>
    <td align="center"><b>DarkJota & PeHenrii</b><br><sub>Eventbus & Base</sub></td>
  </tr>
</table>