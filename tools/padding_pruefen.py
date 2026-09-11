#!/usr/bin/env python3
"""
Findet ungueltige Modifier.padding(...)-Kombinationen.

Compose kennt vier Ueberladungen:
    padding(all: Dp)
    padding(horizontal: Dp = 0.dp, vertical: Dp = 0.dp)
    padding(start: Dp = 0.dp, top: Dp = 0.dp, end: Dp = 0.dp, bottom: Dp = 0.dp)
    padding(paddingValues: PaddingValues)

start/end und horizontal/vertical duerfen sich also nicht mischen. Das ist ein
Typfehler, kein Syntaxfehler - der Kotlin-Parser sieht ihn nicht, nur der
Compiler. Genau das hat den ersten echten Build zum Scheitern gebracht
(OnboardingScreen.kt:224, siehe Commit-Historie).
"""
import os
import re
import sys

ROOT = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "app", "src", "main", "java")


def klammer_inhalt(text, start_klammer):
    """Liefert den Inhalt von ( bis zur passenden ), ab dem Index der '('."""
    tiefe = 0
    for i in range(start_klammer, len(text)):
        if text[i] == "(":
            tiefe += 1
        elif text[i] == ")":
            tiefe -= 1
            if tiefe == 0:
                return text[start_klammer + 1:i]
    return None


gesamt = 0
for dirpath, _, names in os.walk(ROOT):
    for n in sorted(names):
        if not n.endswith(".kt"):
            continue
        f = os.path.join(dirpath, n)
        text = open(f, encoding="utf-8").read()
        for m in re.finditer(r"\.padding\s*\(", text):
            args = klammer_inhalt(text, m.end() - 1)
            if args is None:
                continue
            hat_start = re.search(r"(?<![\w.])start\s*=", args)
            hat_end = re.search(r"(?<![\w.])end\s*=", args)
            hat_top = re.search(r"(?<![\w.])top\s*=", args)
            hat_bottom = re.search(r"(?<![\w.])bottom\s*=", args)
            hat_horiz = re.search(r"(?<![\w.])horizontal\s*=", args)
            hat_vert = re.search(r"(?<![\w.])vertical\s*=", args)
            ungueltig = ((hat_start or hat_end) and (hat_horiz or hat_vert)) or \
                        ((hat_top or hat_bottom) and (hat_horiz or hat_vert))
            if ungueltig:
                zeile = text[:m.start()].count("\n") + 1
                print(f"{os.path.relpath(f, ROOT)}:{zeile}")
                print("   ", " ".join(args.split()))
                gesamt += 1

print("---")
print(f"{gesamt} ungueltige padding()-Kombinationen")
sys.exit(1 if gesamt else 0)
