#!/usr/bin/env python3
"""Findet Importe, deren einfacher Name im Dateikoerper nicht vorkommt."""
import os, re, sys
ROOT = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "app", "src", "main", "java")
schreiben = "--fix" in sys.argv

def strip(src):
    """Nur Kommentare entfernen. Zeichenketten bleiben stehen, weil
    String-Templates ${...} echte Aufrufe enthalten koennen."""
    src = re.sub(r"/\*(?:.|\n)*?\*/", "", src)
    src = re.sub(r"//[^\n]*", "", src)
    return src

gesamt = 0
for dirpath, _, names in os.walk(ROOT):
    for n in sorted(names):
        if not n.endswith(".kt"):
            continue
        f = os.path.join(dirpath, n)
        zeilen = open(f, encoding="utf-8").read().split("\n")
        koerper = strip("\n".join(z for z in zeilen if not z.startswith("import ")))
        raus = []
        for i, z in enumerate(zeilen):
            m = re.match(r'import\s+([\w.]+)(?:\s+as\s+(\w+))?\s*$', z)
            if not m:
                continue
            name = m.group(2) or m.group(1).split(".")[-1]
            if name == "*":
                continue
            if not re.search(r'(?<![\w])' + re.escape(name) + r'(?![\w])', koerper):
                raus.append(i)
        if raus:
            gesamt += len(raus)
            print(os.path.relpath(f, ROOT), f"({len(raus)})")
            for i in raus:
                print("   ", zeilen[i])
            if schreiben:
                neu = [z for i, z in enumerate(zeilen) if i not in set(raus)]
                open(f, "w", encoding="utf-8").write("\n".join(neu))
print("---")
print(f"{gesamt} ungenutzte Importe" + (" entfernt" if schreiben else ""))
