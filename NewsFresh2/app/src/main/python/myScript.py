def ConvertSectoDay(n):
	day = n // (24 * 3600)
	n = n % (24 * 3600)
	hour = n // 3600
	n %= 3600
	minutes = n // 60
	n %= 60
	seconds = n
	print(int(day),"days", int(hour), "hours", int(minutes), "minutes", int(seconds), "seconds")



ConvertSectoDay(n)
