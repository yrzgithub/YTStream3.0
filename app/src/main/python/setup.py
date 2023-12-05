from os import system
from os import getcwd

modules = []

with open("requirements.txt") as file:
	modules.extend(file.read().split("\n"))
	file.close()

for module in modules:
	print(f"Installing {module}")
	system(f"pip install {module} -t .")

input()