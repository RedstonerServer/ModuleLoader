command modules {
	list {
		help Lists all modules. Color indicates status: §aENABLED §cDISABLED;
		perm jutils.modules.list; 
		run list;
    }
    load [string:name...] {
        help (Re)-Loads a module. WARNING: Handle with care! This has direct affect on code being executed. This command will temporarily halt the main thread until the class loading operation was completed.;
        perm jtuils.modules.admin;
        run load name;
        type console;
    }
    unload [string:name...] {
        help Unloads a module. WARNING: Handle with care! This has direct affect on code being executed. This command will temporarily halt the main thread until the class loading operation was completed.;
        perm jutils.modules.admin;
        run unload name;
        type console;
    }
}