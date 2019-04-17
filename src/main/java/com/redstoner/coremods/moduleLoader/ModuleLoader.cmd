command modules {
    [empty] {
        help Lists all modules. Color indicates status: §aENABLED §cDISABLED;
		perm moduleloader.modules.list; 
		run list;
    }
    -v {
        help Lists all modules and their versions. Color indicates status: §aENABLED §cDISABLED;
		perm moduleloader.modules.list;
		run listversions;
    }


	list {
		help Lists all modules. Color indicates status: §aENABLED §cDISABLED;
		perm moduleloader.modules.list; 
		run list;
    }
	list -v {
		help Lists all modules and their versions. Color indicates status: §aENABLED §cDISABLED;
		perm moduleloader.modules.list;
		run listversions;
    }
    load [string:name...] {
        help (Re)-Loads a module. WARNING: Handle with care! This has direct affect on code being executed. This command will temporarily halt the main thread until the class loading operation was completed.;
        perm moduleloader.modules.admin;
        run load name;
        type console;
    }
    unload [string:name...] {
        help Unloads a module. WARNING: Handle with care! This has direct affect on code being executed. This command will temporarily halt the main thread until the class loading operation was completed.;
        perm moduleloader.modules.admin;
        run unload name;
        type console;
    }
}